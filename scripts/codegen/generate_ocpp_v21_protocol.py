#!/usr/bin/env python3
"""Generate the OCPP 2.1 protocol layer (message DTOs + block dispatchers) for
com.monta.ocpp.emulator.ocpp.v21.protocol from the official OCPP 2.1 JSON schemas.

Usage:
    python3 scripts/codegen/generate_ocpp_v21_protocol.py

Regenerate whenever the schemas under
app/src/jvmMain/resources/ocpp/schemas/2.1/ change. This script is idempotent:
it fully rewrites every file it owns (see OWNED_DIRS below) on every run.
"""
from __future__ import annotations

import json
import re
from dataclasses import dataclass, field
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
SCHEMA_DIR = REPO_ROOT / "app/src/jvmMain/resources/ocpp/schemas/2.1"
KOTLIN_ROOT = REPO_ROOT / "app/src/jvmMain/kotlin/com/monta/ocpp/emulator/ocpp/v21/protocol"

MESSAGES_PKG = "com.monta.ocpp.emulator.ocpp.v21.protocol.messages"
COMMON_PKG = f"{MESSAGES_PKG}.common"
BLOCKS_PKG = "com.monta.ocpp.emulator.ocpp.v21.protocol.blocks"
ERROR_PKG = "com.monta.ocpp.emulator.ocpp.v21.protocol.error"
CLIENT_PKG = "com.monta.ocpp.emulator.ocpp.v21.protocol.client"

OWNED_DIRS = [
    KOTLIN_ROOT / "messages",
    KOTLIN_ROOT / "blocks",
    KOTLIN_ROOT / "client",
]

# ---------------------------------------------------------------------------
# Action direction + block grouping table
# ---------------------------------------------------------------------------
# direction: "out" = CP -> CSMS (we implement Sender, expose via OcppClientV21.asXxx)
#            "in"  = CSMS -> CP (we implement Listener, handled by emulator profile handlers)
#            "both" = DataTransfer only, needs both directions in one dispatcher.

BLOCKS: list[tuple[str, str, list[tuple[str, str]]]] = [
    ("authorization", "Authorization", [
        ("Authorize", "out"),
    ]),
    ("availability", "Availability", [
        ("Heartbeat", "out"),
        ("StatusNotification", "out"),
        ("ChangeAvailability", "in"),
    ]),
    ("certificatemanagement", "CertificateManagement", [
        ("Get15118EVCertificate", "out"),
        ("GetCertificateStatus", "out"),
        ("GetCertificateChainStatus", "out"),
        ("SignCertificate", "out"),
        ("CertificateSigned", "in"),
        ("DeleteCertificate", "in"),
        ("GetInstalledCertificateIds", "in"),
        ("InstallCertificate", "in"),
    ]),
    ("datatransfer", "DataTransfer", [
        ("DataTransfer", "both"),
    ]),
    ("diagnostics", "Diagnostics", [
        ("LogStatusNotification", "out"),
        ("NotifyCustomerInformation", "out"),
        ("NotifyEvent", "out"),
        ("NotifyMonitoringReport", "out"),
        ("ClearVariableMonitoring", "in"),
        ("CustomerInformation", "in"),
        ("GetLog", "in"),
        ("GetMonitoringReport", "in"),
        ("SetMonitoringBase", "in"),
        ("SetMonitoringLevel", "in"),
        ("SetVariableMonitoring", "in"),
    ]),
    ("displaymessage", "DisplayMessage", [
        ("ClearDisplayMessage", "in"),
        ("GetDisplayMessages", "in"),
        ("SetDisplayMessage", "in"),
        ("NotifyDisplayMessages", "out"),
    ]),
    ("firmwaremanagement", "FirmwareManagement", [
        ("FirmwareStatusNotification", "out"),
        ("PublishFirmwareStatusNotification", "out"),
        ("PublishFirmware", "in"),
        ("UnpublishFirmware", "in"),
        ("UpdateFirmware", "in"),
    ]),
    ("localauthorizationlistmanagement", "LocalAuthorizationListManagement", [
        ("ClearCache", "in"),
        ("GetLocalListVersion", "in"),
        ("SendLocalList", "in"),
    ]),
    ("metervalues", "MeterValues", [
        ("MeterValues", "out"),
    ]),
    ("provisioning", "Provisioning", [
        ("BootNotification", "out"),
        ("NotifyReport", "out"),
        ("GetBaseReport", "in"),
        ("GetReport", "in"),
        ("GetVariables", "in"),
        ("Reset", "in"),
        ("SetNetworkProfile", "in"),
        ("SetVariables", "in"),
    ]),
    ("remotecontrol", "RemoteControl", [
        ("RequestStartTransaction", "in"),
        ("RequestStopTransaction", "in"),
        ("TriggerMessage", "in"),
        ("UnlockConnector", "in"),
    ]),
    ("reservation", "Reservation", [
        ("ReservationStatusUpdate", "out"),
        ("CancelReservation", "in"),
        ("ReserveNow", "in"),
    ]),
    ("security", "Security", [
        ("SecurityEventNotification", "out"),
    ]),
    ("smartcharging", "SmartCharging", [
        ("ClearedChargingLimit", "out"),
        ("NotifyChargingLimit", "out"),
        ("NotifyEVChargingNeeds", "out"),
        ("NotifyEVChargingSchedule", "out"),
        ("ReportChargingProfiles", "out"),
        ("NotifyAllowedEnergyTransfer", "out"),
        ("ClearChargingProfile", "in"),
        ("GetChargingProfiles", "in"),
        ("GetCompositeSchedule", "in"),
        ("SetChargingProfile", "in"),
    ]),
    ("tariffandcost", "TariffAndCost", [
        ("CostUpdated", "in"),
    ]),
    ("transactions", "Transactions", [
        ("TransactionEvent", "out"),
        ("GetTransactionStatus", "in"),
    ]),
    # -- OCPP 2.1-only blocks --
    ("tariff", "Tariff", [
        ("SetDefaultTariff", "in"),
        ("GetTariffs", "in"),
        ("ClearTariffs", "in"),
        ("ChangeTransactionTariff", "in"),
        ("VatNumberValidation", "out"),
    ]),
    ("der", "Der", [
        ("SetDERControl", "in"),
        ("GetDERControl", "in"),
        ("ClearDERControl", "in"),
        ("NotifyDERAlarm", "out"),
        ("NotifyDERStartStop", "out"),
        ("ReportDERControl", "out"),
    ]),
    ("periodiceventstream", "PeriodicEventStream", [
        ("OpenPeriodicEventStream", "in"),
        ("ClosePeriodicEventStream", "in"),
        ("GetPeriodicEventStream", "in"),
        ("AdjustPeriodicEventStream", "in"),
        ("NotifyPeriodicEventStream", "out"),
    ]),
    ("prioritycharging", "PriorityCharging", [
        ("UsePriorityCharging", "in"),
        ("NotifyPriorityCharging", "out"),
    ]),
    ("dynamicschedule", "DynamicSchedule", [
        ("UpdateDynamicSchedule", "in"),
        ("PullDynamicScheduleUpdate", "out"),
    ]),
    ("batteryswap", "BatterySwap", [
        ("RequestBatterySwap", "in"),
        ("BatterySwap", "out"),
    ]),
    ("settlement", "Settlement", [
        ("NotifySettlement", "out"),
        ("NotifyWebPaymentStarted", "out"),
    ]),
    ("afrr", "Afrr", [
        ("AFRRSignal", "in"),
    ]),
]

ALL_ACTIONS = [action for _, _, actions in BLOCKS for action, _ in actions]

# ---------------------------------------------------------------------------
# Schema loading helpers
# ---------------------------------------------------------------------------


def load_schema(name: str) -> dict:
    path = SCHEMA_DIR / f"{name}.json"
    return json.loads(path.read_text())


def kotlin_name_for_def(def_name: str, def_body: dict) -> str:
    java_type = def_body.get("javaType")
    if java_type:
        return java_type
    if def_name.endswith("Type"):
        return def_name[:-4]
    return def_name


IDENT_RE = re.compile(r"[^0-9A-Za-z]")


def sanitize_enum_ident(raw: str) -> str:
    ident = IDENT_RE.sub("", raw)
    if not ident:
        ident = "Value"
    if ident[0].isdigit():
        ident = "N" + ident
    return ident


def to_camel_method_name(action: str) -> str:
    return action[0].lower() + action[1:]


# ---------------------------------------------------------------------------
# Common type pool
# ---------------------------------------------------------------------------


@dataclass
class EnumDef:
    name: str
    values: list[str] = field(default_factory=list)

    def add_values(self, values: list[str]) -> None:
        for v in values:
            if v not in self.values:
                self.values.append(v)


@dataclass
class ClassDef:
    name: str
    # prop_name -> (schema_node, description)
    properties: dict[str, dict] = field(default_factory=dict)
    required_counts: dict[str, int] = field(default_factory=dict)
    occurrence_count: int = 0
    prop_order: list[str] = field(default_factory=list)


common_enums: dict[str, EnumDef] = {}
common_classes: dict[str, ClassDef] = {}


def register_definition(def_name: str, def_body: dict) -> None:
    name = kotlin_name_for_def(def_name, def_body)
    if "enum" in def_body:
        enum_def = common_enums.setdefault(name, EnumDef(name=name))
        enum_def.add_values(def_body["enum"])
        return

    if def_body.get("type") == "object" or "properties" in def_body:
        class_def = common_classes.setdefault(name, ClassDef(name=name))
        class_def.occurrence_count += 1
        required = set(def_body.get("required", []))
        for prop_name, prop_schema in def_body.get("properties", {}).items():
            if prop_name not in class_def.properties:
                class_def.properties[prop_name] = prop_schema
                class_def.prop_order.append(prop_name)
            if prop_name in required:
                class_def.required_counts[prop_name] = class_def.required_counts.get(prop_name, 0) + 1


def collect_definitions() -> None:
    for name in ALL_ACTIONS:
        for suffix in ("Request", "Response"):
            path = SCHEMA_DIR / f"{name}{suffix}.json"
            if not path.exists():
                continue
            schema = json.loads(path.read_text())
            for def_name, def_body in schema.get("definitions", {}).items():
                register_definition(def_name, def_body)


# ---------------------------------------------------------------------------
# Type resolution
# ---------------------------------------------------------------------------


@dataclass
class KType:
    kotlin: str
    common_refs: set[str] = field(default_factory=set)
    needs_zoned_date_time: bool = False


def resolve_ref_name(ref: str) -> str:
    # "#/definitions/ChargingStationType" -> "ChargingStationType"
    return ref.rsplit("/", 1)[-1]


def resolve_type(schema_node: dict, local_defs: dict[str, dict]) -> KType:
    if "$ref" in schema_node:
        def_name = resolve_ref_name(schema_node["$ref"])
        def_body = local_defs.get(def_name, {})
        name = kotlin_name_for_def(def_name, def_body)
        if name in common_enums:
            return KType(kotlin=name, common_refs={name})
        if name in common_classes:
            return KType(kotlin=name, common_refs={name})
        # Fallback: unknown ref, should not normally happen.
        return KType(kotlin="String")

    node_type = schema_node.get("type")

    if node_type == "array":
        items = schema_node.get("items", {})
        item_type = resolve_type(items, local_defs)
        return KType(
            kotlin=f"List<{item_type.kotlin}>",
            common_refs=item_type.common_refs,
            needs_zoned_date_time=item_type.needs_zoned_date_time,
        )

    if node_type == "string":
        if schema_node.get("format") == "date-time":
            return KType(kotlin="ZonedDateTime", needs_zoned_date_time=True)
        return KType(kotlin="String")

    if node_type == "integer":
        return KType(kotlin="Int")

    if node_type == "number":
        return KType(kotlin="Double")

    if node_type == "boolean":
        return KType(kotlin="Boolean")

    # No "type" at all: OCA deliberately left this schema-less (e.g. DataTransfer.data is
    # "Data without specified length or format"). This is the one legitimate `Any` escape hatch.
    if node_type is None:
        return KType(kotlin="Any")

    # Should not happen given the 2.1 schema set (no inline objects, no oneOf/anyOf).
    return KType(kotlin="String")


# ---------------------------------------------------------------------------
# Kotlin rendering
# ---------------------------------------------------------------------------

HEADER = "// GENERATED CODE - DO NOT EDIT BY HAND.\n// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py\n"


def sort_imports(import_fqns: set[str] | list[str]) -> list[str]:
    """Order imports the way ktlint's standard:import-ordering expects for this repo's
    .editorconfig layout: `*, java.**, javax.**, kotlin.**, ^` - i.e. everything else
    (alphabetically), then java.*, then javax.*, then kotlin.* (each alphabetical)."""

    def group(fqn: str) -> int:
        if fqn.startswith("java."):
            return 1
        if fqn.startswith("javax."):
            return 2
        if fqn.startswith("kotlin."):
            return 3
        return 0

    return sorted(set(import_fqns), key=lambda fqn: (group(fqn), fqn))


def render_kdoc(description: str | None, indent: str = "") -> str:
    if not description:
        return ""
    text = " ".join(description.replace("\r\n", " ").replace("\n", " ").split())
    return f"{indent}/** {text} */\n"


_ENUM_ENTRY_PASCAL = re.compile(r"^[A-Z][a-zA-Z0-9]*$")


def render_enum_file(enum_def: EnumDef) -> str:
    lines = [HEADER, f"package {COMMON_PKG}", ""]
    needs_json_property = any(sanitize_enum_ident(v) != v for v in enum_def.values)
    if needs_json_property:
        lines.append("import com.fasterxml.jackson.annotation.JsonProperty")
        lines.append("")

    seen_idents: dict[str, int] = {}
    rendered_values = []
    for raw in enum_def.values:
        ident = sanitize_enum_ident(raw)
        if ident in seen_idents:
            seen_idents[ident] += 1
            ident = f"{ident}{seen_idents[ident]}"
        else:
            seen_idents[ident] = 0
        rendered_values.append((ident, raw))

    # OCPP schemas sometimes use lowercase wire literals (e.g. DataEnum.string).
    if any(not _ENUM_ENTRY_PASCAL.match(ident) for ident, _ in rendered_values):
        lines.append("// Wire values may not be UpperCamelCase; keep exact identifiers for Jackson.")
        lines.append('@Suppress("EnumEntryName")')

    lines.append(f"enum class {enum_def.name} {{")

    body_lines = []
    for ident, raw in rendered_values:
        if ident != raw:
            body_lines.append(f'    @JsonProperty("{raw}")')
        body_lines.append(f"    {ident},")
    lines.append("\n".join(body_lines))
    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def order_properties(prop_names: list[str], required: set[str]) -> list[str]:
    req = [p for p in prop_names if p in required]
    opt = [p for p in prop_names if p not in required]
    return req + opt


def render_property_lines(
    prop_names: list[str],
    properties: dict[str, dict],
    required: set[str],
    local_defs: dict[str, dict],
    common_refs_out: set[str],
    needs_zdt_out: list[bool],
) -> list[str]:
    lines = []
    ordered = order_properties(prop_names, required)
    for prop_name in ordered:
        prop_schema = properties[prop_name]
        ktype = resolve_type(prop_schema, local_defs)
        common_refs_out.update(ktype.common_refs)
        if ktype.needs_zoned_date_time:
            needs_zdt_out[0] = True
        is_required = prop_name in required
        kotlin_prop = escape_kotlin_identifier(prop_name)
        doc = render_kdoc(prop_schema.get("description"), indent="    ")
        if doc:
            lines.append(doc.rstrip("\n"))
        if is_required:
            lines.append(f"    val {kotlin_prop}: {ktype.kotlin},")
        else:
            lines.append(f"    val {kotlin_prop}: {ktype.kotlin}? = null,")
    return lines


KOTLIN_KEYWORDS = {"data", "in", "is", "object", "typeof", "package", "class", "interface", "fun"}


def escape_kotlin_identifier(name: str) -> str:
    if name in KOTLIN_KEYWORDS:
        return f"`{name}`"
    return name


def render_class_file(class_def: ClassDef) -> str:
    required = {p for p, count in class_def.required_counts.items() if count == class_def.occurrence_count}
    common_refs: set[str] = set()
    needs_zdt = [False]
    prop_lines = render_property_lines(
        class_def.prop_order,
        class_def.properties,
        required,
        local_defs={},  # not used for common classes: refs already resolved to common names
        common_refs_out=common_refs,
        needs_zdt_out=needs_zdt,
    )
    common_refs.discard(class_def.name)

    # common_refs are always in this same COMMON_PKG, so no import statements are needed for them.
    lines = [HEADER, f"package {COMMON_PKG}", ""]
    if needs_zdt[0]:
        lines.append("import java.time.ZonedDateTime")
        lines.append("")
    if not prop_lines:
        lines.append(f"class {class_def.name}")
    else:
        lines.append(f"data class {class_def.name}(")
        lines.extend(prop_lines)
        lines.append(")")
    lines.append("")
    return "\n".join(lines)


def write_common_types() -> None:
    common_dir = KOTLIN_ROOT / "messages" / "common"
    common_dir.mkdir(parents=True, exist_ok=True)
    for enum_def in common_enums.values():
        (common_dir / f"{enum_def.name}.kt").write_text(render_enum_file(enum_def))
    for class_def in sorted(common_classes.values(), key=lambda c: c.name):
        (common_dir / f"{class_def.name}.kt").write_text(render_class_file(class_def))


# ---------------------------------------------------------------------------
# Per-action message file rendering
# ---------------------------------------------------------------------------


def synthetic_notify_periodic_event_stream_response() -> dict:
    return {
        "definitions": {
            "CustomDataType": {
                "javaType": "CustomData",
                "type": "object",
                "properties": {"vendorId": {"type": "string", "maxLength": 255}},
                "required": ["vendorId"],
            }
        },
        "type": "object",
        "properties": {"customData": {"$ref": "#/definitions/CustomDataType"}},
        "required": [],
    }


def render_message_root(
    class_name: str,
    schema: dict,
    interface_name: str,
    common_refs_out: set[str],
    needs_zdt_out: list[bool],
) -> list[str]:
    local_defs = schema.get("definitions", {})
    properties = schema.get("properties", {})
    required = set(schema.get("required", []))
    prop_names = list(properties.keys())

    prop_lines = render_property_lines(
        prop_names,
        properties,
        required,
        local_defs,
        common_refs_out,
        needs_zdt_out,
    )

    lines = []
    doc = render_kdoc(schema.get("comment"))
    if doc:
        lines.append(doc.rstrip("\n"))
    if not prop_lines:
        lines.append(f"class {class_name} : {interface_name}")
    else:
        lines.append(f"data class {class_name}(")
        lines.extend(prop_lines)
        lines.append(f") : {interface_name}")
    return lines


def render_action_file(action: str) -> str:
    request_schema = load_schema(f"{action}Request") if (SCHEMA_DIR / f"{action}Request.json").exists() else None
    if action == "NotifyPeriodicEventStream":
        # Only a request-shaped schema is published (no confirmation payload defined by OCA);
        # we still model an (empty) response so it fits the OcppRequest/OcppConfirmation contract.
        request_schema = load_schema("NotifyPeriodicEventStream")
        response_schema = synthetic_notify_periodic_event_stream_response()
    else:
        response_schema = load_schema(f"{action}Response")

    common_refs: set[str] = set()
    needs_zdt = [False]

    request_lines = render_message_root(f"{action}Request", request_schema, "OcppRequest", common_refs, needs_zdt)
    response_lines = render_message_root(f"{action}Response", response_schema, "OcppConfirmation", common_refs, needs_zdt)

    import_fqns = {
        "com.monta.library.ocpp.common.profile.Feature",
        "com.monta.library.ocpp.common.profile.OcppConfirmation",
        "com.monta.library.ocpp.common.profile.OcppRequest",
    }
    import_fqns.update(f"{COMMON_PKG}.{ref}" for ref in common_refs)
    if needs_zdt[0]:
        import_fqns.add("java.time.ZonedDateTime")

    lines = [HEADER, f"package {MESSAGES_PKG}", ""]
    for fqn in sort_imports(import_fqns):
        lines.append(f"import {fqn}")
    lines.append("")
    lines.append(f"object {action}Feature : Feature {{")
    lines.append(f'    override val name: String = "{action}"')
    lines.append(f"    override val requestType: Class<out OcppRequest> = {action}Request::class.java")
    lines.append(f"    override val confirmationType: Class<out OcppConfirmation> = {action}Response::class.java")
    lines.append("}")
    lines.append("")
    lines.extend(request_lines)
    lines.append("")
    lines.extend(response_lines)
    lines.append("")
    return "\n".join(lines)


def write_action_messages() -> None:
    messages_dir = KOTLIN_ROOT / "messages"
    messages_dir.mkdir(parents=True, exist_ok=True)
    for action in ALL_ACTIONS:
        (messages_dir / f"{action}.kt").write_text(render_action_file(action))


# ---------------------------------------------------------------------------
# Block dispatcher rendering
# ---------------------------------------------------------------------------


def render_block_file(pkg: str, class_prefix: str, actions: list[tuple[str, str]]) -> str:
    features_val = f"{pkg}Features"
    listener_actions = [a for a, d in actions if d in ("in", "both")]
    sender_actions = [a for a, d in actions if d in ("out", "both")]

    import_fqns = {
        "com.monta.library.ocpp.common.profile.OcppConfirmation",
        "com.monta.library.ocpp.common.profile.OcppRequest",
        "com.monta.library.ocpp.common.profile.ProfileDispatcher",
        "com.monta.library.ocpp.common.session.OcppSession",
        "com.monta.library.ocpp.common.transport.OcppCallException",
        f"{ERROR_PKG}.MessageErrorCodeV21",
    }
    for action, _ in actions:
        import_fqns.add(f"{MESSAGES_PKG}.{action}Feature")
        import_fqns.add(f"{MESSAGES_PKG}.{action}Request")
        import_fqns.add(f"{MESSAGES_PKG}.{action}Response")

    lines = [HEADER, f"package {BLOCKS_PKG}.{pkg}", ""]
    for fqn in sort_imports(import_fqns):
        lines.append(f"import {fqn}")
    lines.append("")

    lines.append(f"val {features_val} = listOf(")
    for action, _ in actions:
        lines.append(f"    {action}Feature,")
    lines.append(")")
    lines.append("")

    has_listener = len(listener_actions) > 0
    ctor = f"class {class_prefix}ClientDispatcher(\n    private val listener: Listener,\n) : ProfileDispatcher {{" if has_listener else f"class {class_prefix}ClientDispatcher : ProfileDispatcher {{"
    lines.append(ctor)
    lines.append(f"    override val featureList = {features_val}")
    lines.append("")
    lines.append("    override suspend fun handleRequest(")
    lines.append("        ocppSessionInfo: OcppSession.Info,")
    lines.append("        request: OcppRequest,")
    lines.append("    ): OcppConfirmation {")
    if has_listener:
        lines.append("        return when (request) {")
        for action in listener_actions:
            method = to_camel_method_name(action)
            lines.append(f"            is {action}Request -> listener.{method}(ocppSessionInfo, request)")
        lines.append(
            '            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")'
        )
        lines.append("        }")
    else:
        lines.append(
            '        throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")'
        )
    lines.append("    }")

    if has_listener:
        lines.append("")
        lines.append("    interface Listener {")
        for i, action in enumerate(listener_actions):
            method = to_camel_method_name(action)
            lines.append(f"        suspend fun {method}(")
            lines.append("            ocppSessionInfo: OcppSession.Info,")
            lines.append(f"            request: {action}Request,")
            lines.append(f"        ): {action}Response")
            if i != len(listener_actions) - 1:
                lines.append("")
        lines.append("    }")

    if sender_actions:
        lines.append("")
        lines.append("    interface Sender {")
        for i, action in enumerate(sender_actions):
            method = to_camel_method_name(action)
            lines.append(f"        suspend fun {method}(")
            lines.append(f"            request: {action}Request,")
            lines.append(f"        ): {action}Response")
            if i != len(sender_actions) - 1:
                lines.append("")
        lines.append("    }")

    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def write_blocks() -> None:
    blocks_dir = KOTLIN_ROOT / "blocks"
    blocks_dir.mkdir(parents=True, exist_ok=True)
    for pkg, class_prefix, actions in BLOCKS:
        pkg_dir = blocks_dir / pkg
        pkg_dir.mkdir(parents=True, exist_ok=True)
        (pkg_dir / f"{class_prefix}Block.kt").write_text(render_block_file(pkg, class_prefix, actions))


# ---------------------------------------------------------------------------
# OcppClientV21 + Builder rendering
# ---------------------------------------------------------------------------


def render_client_file() -> str:
    import_fqns = {
        "com.monta.library.ocpp.client.OcppClient",
        "com.monta.library.ocpp.common.OcppClientConnectionEvent",
        "com.monta.library.ocpp.common.OcppClientDisconnectionEvent",
        "com.monta.library.ocpp.common.profile.ProfileDispatcher",
        "com.monta.library.ocpp.common.serialization.SerializationMode",
        "com.monta.library.ocpp.common.session.OcppSession",
        "com.monta.library.ocpp.common.session.OcppSessionRepository",
        "com.monta.library.ocpp.common.transport.OcppSettings",
        f"{ERROR_PKG}.OcppErrorResponderV21",
    }
    for pkg, class_prefix, actions in BLOCKS:
        sender_actions = [a for a, d in actions if d in ("out", "both")]
        if not sender_actions:
            continue
        import_fqns.add(f"{BLOCKS_PKG}.{pkg}.{class_prefix}ClientDispatcher")
        for action in sender_actions:
            import_fqns.add(f"{MESSAGES_PKG}.{action}Request")
            import_fqns.add(f"{MESSAGES_PKG}.{action}Response")

    lines = [HEADER, f"package {CLIENT_PKG}", ""]
    for fqn in sort_imports(import_fqns):
        lines.append(f"import {fqn}")
    lines.append("")
    lines.append("class OcppClientV21(")
    lines.append("    onConnect: OcppClientConnectionEvent,")
    lines.append("    onDisconnect: OcppClientDisconnectionEvent,")
    lines.append("    ocppSessionRepository: OcppSessionRepository,")
    lines.append("    settings: OcppSettings,")
    lines.append("    profiles: Set<ProfileDispatcher>,")
    lines.append("    sendHook: suspend (String, String) -> String?,")
    lines.append(") : OcppClient(")
    lines.append("    onConnect = onConnect,")
    lines.append("    onDisconnect = onDisconnect,")
    lines.append("    ocppSessionRepository = ocppSessionRepository,")
    lines.append("    serializationMode = SerializationMode.OCPP_2,")
    lines.append("    ocppErrorResponder = OcppErrorResponderV21,")
    lines.append("    settings = settings,")
    lines.append("    profiles = profiles,")
    lines.append("    sendHook = sendHook,")
    lines.append(") {")

    block_entries = [(pkg, class_prefix, actions) for pkg, class_prefix, actions in BLOCKS if [a for a, d in actions if d in ("out", "both")]]
    for i, (pkg, class_prefix, actions) in enumerate(block_entries):
        sender_actions = [a for a, d in actions if d in ("out", "both")]
        lines.append(f"    fun as{class_prefix}(")
        lines.append("        ocppSessionInfo: OcppSession.Info,")
        lines.append(f"    ): {class_prefix}ClientDispatcher.Sender {{")
        lines.append(f"        return object : {class_prefix}ClientDispatcher.Sender {{")
        for j, action in enumerate(sender_actions):
            method = to_camel_method_name(action)
            lines.append(f"            override suspend fun {method}(")
            lines.append(f"                request: {action}Request,")
            lines.append(f"            ) = send(ocppSessionInfo, request) as {action}Response")
            if j != len(sender_actions) - 1:
                lines.append("")
        lines.append("        }")
        lines.append("    }")
        if i != len(block_entries) - 1:
            lines.append("")
    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def render_builder_file() -> str:
    import_fqns = {"com.monta.library.ocpp.client.BaseOcppClientBuilder"}
    for pkg, class_prefix, actions in BLOCKS:
        import_fqns.add(f"{BLOCKS_PKG}.{pkg}.{class_prefix}ClientDispatcher")

    lines = [HEADER, f"package {CLIENT_PKG}", ""]
    for fqn in sort_imports(import_fqns):
        lines.append(f"import {fqn}")
    lines.append("")
    lines.append("class OcppClientV21Builder : BaseOcppClientBuilder<OcppClientV21Builder>() {")
    for i, (pkg, class_prefix, actions) in enumerate(BLOCKS):
        listener_actions = [a for a, d in actions if d in ("in", "both")]
        method_name = f"add{class_prefix}"
        if listener_actions:
            lines.append(f"    fun {method_name}(")
            lines.append(f"        listener: {class_prefix}ClientDispatcher.Listener,")
            lines.append("    ): OcppClientV21Builder {")
            lines.append(f"        profiles.add({class_prefix}ClientDispatcher(listener))")
            lines.append("        return this")
            lines.append("    }")
        else:
            lines.append(f"    fun {method_name}(): OcppClientV21Builder {{")
            lines.append(f"        profiles.add({class_prefix}ClientDispatcher())")
            lines.append("        return this")
            lines.append("    }")
        if i != len(BLOCKS) - 1:
            lines.append("")
    lines.append("")
    lines.append("    fun build(): OcppClientV21 {")
    lines.append("        return OcppClientV21(")
    lines.append("            onConnect = requireNotNull(onConnect),")
    lines.append("            onDisconnect = requireNotNull(onDisconnect),")
    lines.append("            ocppSessionRepository = requireNotNull(ocppSessionRepository),")
    lines.append("            settings = requireNotNull(settings),")
    lines.append("            profiles = profiles,")
    lines.append("            sendHook = sendHook,")
    lines.append("        )")
    lines.append("    }")
    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def write_client() -> None:
    client_dir = KOTLIN_ROOT / "client"
    client_dir.mkdir(parents=True, exist_ok=True)
    (client_dir / "OcppClientV21.kt").write_text(render_client_file())
    (client_dir / "OcppClientV21Builder.kt").write_text(render_builder_file())


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------


def clean_owned_dirs() -> None:
    import shutil

    for d in OWNED_DIRS:
        if d.exists():
            shutil.rmtree(d)


def main() -> None:
    clean_owned_dirs()
    collect_definitions()
    write_common_types()
    write_action_messages()
    write_blocks()
    write_client()

    total_common_enum_files = len(common_enums)
    total_common_class_files = len(common_classes)
    total_action_files = len(ALL_ACTIONS)
    total_block_files = len(BLOCKS)

    print(f"actions: {total_action_files}")
    print(f"common enums: {total_common_enum_files}")
    print(f"common classes: {total_common_class_files}")
    print(f"block dispatcher files: {total_block_files}")
    print(
        "total generated .kt files:",
        total_common_enum_files + total_common_class_files + total_action_files + total_block_files,
    )


if __name__ == "__main__":
    main()
