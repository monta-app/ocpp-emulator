rootProject.name = "tool-ocpp-emulator"
include("app")

// Use published JitPack artifacts for library-ocpp (ocpp-core / ocpp-v16 / ocpp-v201).
// Local composite builds are intentionally disabled to avoid flaky sibling-checkout races.
