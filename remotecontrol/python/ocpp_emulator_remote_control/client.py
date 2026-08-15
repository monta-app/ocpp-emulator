"""Client library for the OCPP emulator's control socket.

See docs/cli/cli-plan.md (in the ocpp-emulator repo) for the protocol design.
The emulator listens on 127.0.0.1:9911 by default (override with the
OCPP_EMULATOR_CONTROL_PORT env var on the emulator side) and speaks
newline-delimited JSON: one request object per line in, one response object
per line out.
"""

from __future__ import annotations

import argparse
import json
import socket
from typing import Any

DEFAULT_PORT = 9911

# Wire values for ChargePointStatus / ChargePointErrorCode (see
# com.monta.library.ocpp.v16.core in the emulator's OCPP library). The control socket's
# JSON deserialization is case-sensitive on these exact names, so CLI/library callers are
# matched case-insensitively against this list and normalized before being sent.
CHARGE_POINT_STATUS_VALUES = (
    "Available",
    "Preparing",
    "Charging",
    "SuspendedEVSE",
    "SuspendedEV",
    "Finishing",
    "Reserved",
    "Unavailable",
    "Faulted",
)

CHARGE_POINT_ERROR_CODE_VALUES = (
    "ConnectorLockFailure",
    "EVCommunicationError",
    "GroundFailure",
    "HighTemperature",
    "InternalError",
    "LocalListConflict",
    "NoError",
    "OtherError",
    "OverCurrentFailure",
    "OverVoltage",
    "PowerMeterFailure",
    "PowerSwitchFailure",
    "ReaderFailure",
    "ResetFailure",
    "UnderVoltage",
    "WeakSignal",
)


def _normalize_enum_value(value: str, valid_values: tuple[str, ...], label: str) -> str:
    for valid_value in valid_values:
        if valid_value.lower() == value.lower():
            return valid_value
    raise ValueError(f"invalid {label} '{value}', expected one of: {', '.join(valid_values)}")


def parse_charge_point_connector(value: str) -> tuple[str, int]:
    """Parses "CP" or "CP:connector" (connector defaults to 1)."""
    identity, sep, connector_str = value.partition(":")
    if not sep:
        return identity, 1
    try:
        return identity, int(connector_str)
    except ValueError:
        raise argparse.ArgumentTypeError(
            f"invalid connector in '{value}': expected CP or CP:connector, e.g. CP001 or CP001:2",
        ) from None


class ControlClient:
    """Minimal client for the emulator's control socket.

    Usage as a library, e.g. from an integration test:

        with ControlClient() as client:
            client.connect("CP001")
            client.plug("CP001", connector_id=2)
            client.unplug("CP001", connector_id=2)
            client.disconnect("CP001")
    """

    def __init__(self, host: str = "127.0.0.1", port: int = DEFAULT_PORT, timeout: float = 15.0) -> None:
        # `chargePoint.connect` blocks server-side until the charge point's websocket is
        # up or a 10s internal timeout elapses (see ControlCommandDispatcher.awaitConnected
        # in the emulator) - give that room plus margin for the connection attempt itself.
        self._socket = socket.create_connection((host, port), timeout=timeout)
        self._reader = self._socket.makefile("r", encoding="utf-8", newline="\n")
        self._next_id = 0

    def send(self, command: str, **params: Any) -> dict[str, Any]:
        self._next_id += 1
        request = {"id": str(self._next_id), "command": command, "params": params}

        self._socket.sendall((json.dumps(request) + "\n").encode("utf-8"))

        line = self._reader.readline()
        if not line:
            raise ConnectionError("control server closed the connection")

        response = json.loads(line)

        if not response.get("ok", False):
            error = response.get("error", {})
            raise RuntimeError(f"{error.get('code', 'UNKNOWN_ERROR')}: {error.get('message', '')}")

        return response.get("result") or {}

    def hello(self) -> dict[str, Any]:
        return self.send("hello")

    def connect(self, identity: str) -> dict[str, Any]:
        """Bring the charge point with the given OCPP identity online (connects its
        websocket to the CSMS). Mirrors the GUI's connect/disconnect toggle."""
        return self.send("chargePoint.connect", identity=identity)

    def disconnect(self, identity: str) -> dict[str, Any]:
        """Take the charge point with the given OCPP identity offline (closes its
        websocket to the CSMS). Mirrors the GUI's connect/disconnect toggle."""
        return self.send("chargePoint.disconnect", identity=identity)

    def set_car_state(self, identity: str, car_state: str, connector_id: int = 1) -> dict[str, Any]:
        return self.send("connector.setCarState", identity=identity, carState=car_state, connectorId=connector_id)

    def plug(self, identity: str, connector_id: int = 1) -> dict[str, Any]:
        """Simulate the car being plugged in AND ready to charge - CarState "C", the
        GUI's "Ready" button. Not the same as the GUI's "Plugged" button (CarState "B"),
        which simulates a cable connected but not yet ready."""
        return self.set_car_state(identity, "C", connector_id)

    def unplug(self, identity: str, connector_id: int = 1) -> dict[str, Any]:
        """Simulate the car being unplugged - CarState "A", the GUI's "Unplugged"
        button."""
        return self.set_car_state(identity, "A", connector_id)

    def set_connector_status(
        self,
        identity: str,
        connector_id: int = 1,
        status: str | None = None,
        error: str | None = None,
    ) -> dict[str, Any]:
        """Force a connector's raw status and/or error code - the GUI's "Connector
        Status" dialog. `status`/`error` are independent: pass either or both. Whichever
        is omitted is read from the connector's current state first and resent unchanged,
        matching the GUI dialog (which always pre-fills both fields and sends both)."""
        if status is not None:
            status = _normalize_enum_value(status, CHARGE_POINT_STATUS_VALUES, "status")
        if error is not None:
            error = _normalize_enum_value(error, CHARGE_POINT_ERROR_CODE_VALUES, "error")

        if status is None or error is None:
            current = self.send("connector.getState", identity=identity, connectorId=connector_id)
            status = status if status is not None else current.get("status")
            error = error if error is not None else current.get("errorCode")

        return self.send(
            "connector.setStatus",
            identity=identity,
            connectorId=connector_id,
            status=status,
            errorCode=error,
        )

    def get_connector_status(self, identity: str, connector_id: int = 1) -> dict[str, Any]:
        """Read a connector's current status/error code (and carState/transaction/meter) -
        the read-only counterpart to set_connector_status()."""
        return self.send("connector.getState", identity=identity, connectorId=connector_id)

    def close(self) -> None:
        self._socket.close()

    def __enter__(self) -> "ControlClient":
        return self

    def __exit__(self, *exc_info: object) -> None:
        self.close()
