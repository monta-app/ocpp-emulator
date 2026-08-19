#!/usr/bin/env python3
"""CLI for the OCPP emulator's control socket.

See remotecontrol/remotecontrol-plan.md (in the ocpp-emulator repo) for the protocol design.
The client library lives in ocpp_emulator_remote_control/ alongside this
script (also usable directly, e.g. from a pytest suite: `from
ocpp_emulator_remote_control import ControlClient`).

Run directly to say hello and print the running emulator's version:

    python remotecontrol.py hello [--host HOST] [--port PORT]

Or to bring a charge point online / offline (connect/disconnect its websocket to the CSMS):

    python remotecontrol.py connect CP001 [--host HOST] [--port PORT]
    python remotecontrol.py disconnect CP001 [--host HOST] [--port PORT]

Or to plug/unplug a car at a connector (CP identity, optionally ":connector",
connector defaults to 1). "plug" is the GUI's "Ready" state (car plugged in and
ready to charge) - not "Plugged" (cable connected but not yet ready):

    python remotecontrol.py plug CP001:2 [--host HOST] [--port PORT]
    python remotecontrol.py unplug CP001:2 [--host HOST] [--port PORT]

Or to force a connector's raw status/error code (the GUI's "Connector Status"
dialog) - --status and --error are independent, pass either or both; whichever
one is omitted keeps its current value:

    python remotecontrol.py set-connector-status CP001:2 --status Faulted --error NoError
    python remotecontrol.py set-connector-status CP001:2 --error OverVoltage

Or to read a connector's current status/error code (read-only):

    python remotecontrol.py get-connector-status CP001:2

Or to present an idTag and start a transaction (the GUI's "Authorize" RFID dialog).
Note OCPP 1.6 has no VIN field - only idTag - so a vehicle can't be identified to the
CSMS this way; encode any vehicle correlation into the idTag by convention if needed:

    python remotecontrol.py authorize CP001:2 --id-tag DEADBEEF

Or to stop the connector's active transaction (the GUI's "Stop transaction" button):

    python remotecontrol.py stop-transaction CP001:2
    python remotecontrol.py stop-transaction CP001:2 --reason EVDisconnected
"""

from __future__ import annotations

import argparse

from ocpp_emulator_remote_control import ControlClient, DEFAULT_PORT, parse_charge_point_connector


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
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )

    unplug_parser = subparsers.add_parser(
        "unplug",
        help='simulate a car unplugged (GUI "Unplugged" button)',
    )
    unplug_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )

    set_connector_status_parser = subparsers.add_parser(
        "set-connector-status",
        help='force a connector\'s raw status/error code (GUI "Connector Status" dialog)',
    )
    set_connector_status_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )
    set_connector_status_parser.add_argument("--status", help="e.g. Available, Faulted, ... (see ChargePointStatus)")
    set_connector_status_parser.add_argument(
        "--error",
        help="e.g. NoError, OverVoltage, ... (see ChargePointErrorCode)",
    )

    get_connector_status_parser = subparsers.add_parser(
        "get-connector-status",
        help="read a connector's current status/error code (read-only)",
    )
    get_connector_status_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )

    authorize_parser = subparsers.add_parser(
        "authorize",
        help='present an idTag and start a transaction (GUI "Authorize" RFID dialog)',
    )
    authorize_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )
    authorize_parser.add_argument("--id-tag", required=True, help="RFID idTag to present (max 20 chars)")

    stop_transaction_parser = subparsers.add_parser(
        "stop-transaction",
        help='stop the connector\'s active transaction (GUI "Stop transaction" button)',
    )
    stop_transaction_parser.add_argument(
        "charge_point",
        metavar="CP[:connector]",
        type=parse_charge_point_connector,
        help="e.g. CP001 or CP001:2 (connector defaults to 1)",
    )
    stop_transaction_parser.add_argument(
        "--reason",
        help="e.g. Local, Remote, EVDisconnected, ... (see OCPP Reason); defaults to Local",
    )
    stop_transaction_parser.add_argument("--end-reason-description", help="free-text description, optional")

    args = parser.parse_args()

    if args.action == "set-connector-status" and args.status is None and args.error is None:
        parser.error("set-connector-status requires at least one of --status or --error")

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
        elif args.action == "set-connector-status":
            identity, connector_id = args.charge_point
            result = client.set_connector_status(identity, connector_id, status=args.status, error=args.error)
            print(f"{identity}:{connector_id}: status={result.get('status')} errorCode={result.get('errorCode')}")
        elif args.action == "get-connector-status":
            identity, connector_id = args.charge_point
            result = client.get_connector_status(identity, connector_id)
            print(f"{identity}:{connector_id}: status={result.get('status')} errorCode={result.get('errorCode')}")
        elif args.action == "authorize":
            identity, connector_id = args.charge_point
            result = client.authorize(identity, args.id_tag, connector_id)
            print(f"{identity}:{connector_id}: status={result.get('status')}")
        elif args.action == "stop-transaction":
            identity, connector_id = args.charge_point
            result = client.stop_transaction(
                identity,
                connector_id,
                reason=args.reason,
                end_reason_description=args.end_reason_description,
            )
            print(f"{identity}:{connector_id}: status={result.get('status')} activeTransactionId={result.get('activeTransactionId')}")


if __name__ == "__main__":
    main()
