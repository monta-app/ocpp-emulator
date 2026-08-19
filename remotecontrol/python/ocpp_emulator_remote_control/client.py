"""Client library for the OCPP emulator's control socket.

See remotecontrol/remotecontrol-plan.md (in the ocpp-emulator repo) for the protocol design.
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

# Wire values for OCPP 1.6's StopTransaction `Reason` enum.
REASON_VALUES = (
    "EmergencyStop",
    "EVDisconnected",
    "HardReset",
    "Local",
    "Other",
    "PowerLoss",
    "Reboot",
    "Remote",
    "SoftReset",
    "UnlockCommand",
    "DeAuthorized",
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

    def authorize(self, identity: str, id_tag: str, connector_id: int = 1) -> dict[str, Any]:
        """Present an idTag to the CSMS - the GUI's "Authorize" RFID dialog: an
        Authorize.req/.conf round trip first, and only if accepted, a separate
        StartTransaction.req/.conf to actually start the transaction. This is the
        "RFID tap" pattern - authorize *before* physically plugging in. For the
        plug-in/autocharge pattern (single StartTransaction round trip, no separate
        Authorize.req), see start_transaction(). Returns a dict with the resulting
        `status` (OCPP AuthorizationStatus: Accepted/Blocked/Expired/Invalid/ConcurrentTx).

        Both patterns are fully decided by the CSMS's response - this emulator's Local
        Authorization List only stores what the CSMS pushes down (for reporting back via
        GetLocalListVersion), it never gates an outgoing request. OCPP 1.6's
        Authorize/StartTransaction only carry an idTag (max 20 chars) - there is no VIN
        field in the protocol, so a vehicle can't be identified to the CSMS beyond
        whatever string you pass as `id_tag`.
        """
        return self.send("connector.authorize", identity=identity, idTag=id_tag, connectorId=connector_id)

    def start_transaction(self, identity: str, id_tag: str, connector_id: int = 1) -> dict[str, Any]:
        """Send StartTransaction.req directly, without a preceding Authorize.req - the
        "plug-and-charge"/autocharge pattern: plug in, the Charge Point reads whatever
        identifier the vehicle provides (there's no separate VIN field - it travels as
        `id_tag`, same as authorize()), and one round trip to the CSMS both authorizes and
        starts the transaction. The transaction only actually starts if the CSMS's
        StartTransaction.conf carries an Accepted idTagInfo status. Returns a dict with the
        connector's resulting state (`activeTransactionId` is set when accepted)."""
        return self.send("connector.startTransaction", identity=identity, idTag=id_tag, connectorId=connector_id)

    def stop_transaction(
        self,
        identity: str,
        connector_id: int = 1,
        reason: str | None = None,
        end_reason_description: str | None = None,
    ) -> dict[str, Any]:
        """Stop the connector's active transaction - the GUI's "Stop transaction" button.
        `reason` defaults server-side to "Local" (a regular user-initiated stop) when
        omitted; matched case-insensitively against the OCPP `Reason` enum when given."""
        params: dict[str, Any] = {"identity": identity, "connectorId": connector_id}
        if reason is not None:
            params["reason"] = _normalize_enum_value(reason, REASON_VALUES, "reason")
        if end_reason_description is not None:
            params["endReasonDescription"] = end_reason_description
        return self.send("connector.stopTransaction", **params)

    def close(self) -> None:
        self._socket.close()

    def __enter__(self) -> "ControlClient":
        return self

    def __exit__(self, *exc_info: object) -> None:
        self.close()
