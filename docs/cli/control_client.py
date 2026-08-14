#!/usr/bin/env python3
"""Reference client for the OCPP emulator's control socket.

See cli-plan.md for the protocol design. The emulator listens on
127.0.0.1:9911 by default (override with the OCPP_EMULATOR_CONTROL_PORT env
var on the emulator side) and speaks newline-delimited JSON: one request
object per line in, one response object per line out.

Run directly to say hello and print the running emulator's version:

    python control_client.py [--host HOST] [--port PORT]
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
            client.send("chargePoint.connect", identity="CP001")
            client.send("connector.setCarState", identity="CP001", carState="B")
    """

    def __init__(self, host: str = "127.0.0.1", port: int = DEFAULT_PORT, timeout: float = 5.0) -> None:
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

    def close(self) -> None:
        self._socket.close()

    def __enter__(self) -> "ControlClient":
        return self

    def __exit__(self, *exc_info: object) -> None:
        self.close()


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    args = parser.parse_args()

    with ControlClient(host=args.host, port=args.port) as client:
        result = client.hello()
        print(f"emulator version: {result.get('emulatorVersion')}")
        print(f"protocol version: {result.get('protocolVersion')}")


if __name__ == "__main__":
    main()
