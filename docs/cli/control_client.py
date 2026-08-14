#!/usr/bin/env python3
"""Reference client for the OCPP emulator's control socket.

See cli-plan.md for the protocol design. The emulator listens on
127.0.0.1:9911 by default (override with the OCPP_EMULATOR_CONTROL_PORT env
var on the emulator side) and speaks newline-delimited JSON: one request
object per line in, one response object per line out.

Run directly to say hello and print the running emulator's version:

    python control_client.py hello [--host HOST] [--port PORT]

Or to bring a charge point online / offline (connect/disconnect its websocket to the CSMS):

    python control_client.py connect CP001 [--host HOST] [--port PORT]
    python control_client.py disconnect CP001 [--host HOST] [--port PORT]

Or to plug/unplug a car at a connector (CP identity, optionally ":connector",
connector defaults to 1). "plug" is the GUI's "Ready" state (car plugged in and
ready to charge) - not "Plugged" (cable connected but not yet ready):

    python control_client.py plug CP001:2 [--host HOST] [--port PORT]
    python control_client.py unplug CP001:2 [--host HOST] [--port PORT]

Or to force a connector's raw status/error code (the GUI's "Connector Status"
dialog) - --status and --error are independent, pass either or both; whichever
one is omitted keeps its current value:

    python control_client.py connector-status CP001:2 --status Faulted --error NoError
    python control_client.py connector-status CP001:2 --error OverVoltage
"""

from __future__ import annotations

import argparse
import json
import socket
from typing import Any

DEFAULT_PORT = 9911


def _parse_charge_point_connector(value: str) -> tuple[str, int]:
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

    def connector_status(
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

    def close(self) -> None:
        self._socket.close()

    def __enter__(self) -> "ControlClient":
        return self

    def __exit__(self, *exc_info: object) -> None:
        self.close()


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)

    subparsers = parser.add_subparsers(dest="action", required=True)

    subparsers.add_parser("hello", help="print the running emulator's version")

    connect_parser = subparsers.add_parser("connect", help="bring a charge point online")
    connect_parser.add_argument("identity", help="OCPP identity of the charge point to connect")

    disconnect_parser = subparsers.add_parser("disconnect", help="take a charge point offline")
    disconnect_parser.add_argument("identity", help="OCPP identity of the charge point to disconnect")

    plug_parser = subparsers.add_parser(
        "plug",
        help='simulate a car plugged in and ready to charge (GUI "Ready" button)',
    )
    plug_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=_parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )

    unplug_parser = subparsers.add_parser(
        "unplug",
        help='simulate a car unplugged (GUI "Unplugged" button)',
    )
    unplug_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=_parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )

    connector_status_parser = subparsers.add_parser(
        "connector-status",
        help='force a connector\'s raw status/error code (GUI "Connector Status" dialog)',
    )
    connector_status_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=_parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )
    connector_status_parser.add_argument("--status", help="e.g. Available, Faulted, ... (see ChargePointStatus)")
    connector_status_parser.add_argument("--error", help="e.g. NoError, OverVoltage, ... (see ChargePointErrorCode)")

    args = parser.parse_args()

    if args.action == "connector-status" and args.status is None and args.error is None:
        parser.error("connector-status requires at least one of --status or --error")

    with ControlClient(host=args.host, port=args.port) as client:
        if args.action == "hello":
            result = client.hello()
            print(f"emulator version: {result.get('emulatorVersion')}")
            print(f"protocol version: {result.get('protocolVersion')}")
        elif args.action == "connect":
            result = client.connect(args.identity)
            print(f"{args.identity}: connected={result.get('connected')}")
        elif args.action == "disconnect":
            result = client.disconnect(args.identity)
            print(f"{args.identity}: connected={result.get('connected')}")
        elif args.action == "plug":
            identity, connector_id = args.charge_point
            result = client.plug(identity, connector_id)
            print(f"{identity}:{connector_id}: carState={result.get('carState')}")
        elif args.action == "unplug":
            identity, connector_id = args.charge_point
            result = client.unplug(identity, connector_id)
            print(f"{identity}:{connector_id}: carState={result.get('carState')}")
        elif args.action == "connector-status":
            identity, connector_id = args.charge_point
            result = client.connector_status(identity, connector_id, status=args.status, error=args.error)
            print(f"{identity}:{connector_id}: status={result.get('status')} errorCode={result.get('errorCode')}")


if __name__ == "__main__":
    main()
