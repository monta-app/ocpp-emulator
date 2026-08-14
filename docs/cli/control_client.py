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
"""

from __future__ import annotations

import argparse
import json
import socket
from typing import Any

DEFAULT_PORT = 9911


class ControlClient:
    """Minimal client for the emulator's control socket.

    Usage as a library, e.g. from an integration test:

        with ControlClient() as client:
            client.connect("CP001")
            client.send("connector.setCarState", identity="CP001", carState="B")
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

    args = parser.parse_args()

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


if __name__ == "__main__":
    main()
