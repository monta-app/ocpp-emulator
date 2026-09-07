package com.monta.ocpp.emulator.ocpp.v21.protocol.error

import com.monta.library.ocpp.common.error.OcppErrorCode

/**
 * OCPP 2.1 uses the same CALLERROR error code table as OCPP 2.0.1 (part 4, OCPP-J
 * specification, section "Error Codes"). Copied from library-ocpp's `MessageErrorCodeV201`
 * since :app doesn't have an `ocpp-v21` library dependency to reuse it from.
 */
enum class MessageErrorCodeV21(
    private val message: String,
) : OcppErrorCode {
    FormatViolation("Payload for Action is syntactically incorrect"),
    GenericError("Any other error not covered by the more specific error codes in this table"),
    InternalError("An internal error occurred and the receiver was not able to process the requested Action successfully"),
    MessageTypeNotSupported("A message with an Message Type Number received that is not supported by this implementation."),
    NotImplemented("Requested Action is not known by receiver"),
    NotSupported("Requested Action is recognized but not supported by the receiver"),
    OccurrenceConstraintViolation("Payload for Action is syntactically correct but at least one of the fields violates occurrence constraints"),
    PropertyConstraintViolation("Payload is syntactically correct but at least one field contains an invalid value"),
    ProtocolError("Payload for Action is not conform the PDU structure"),
    RpcFrameworkError("Content of the call is not a valid RPC Request, for example: MessageId could not be read."),
    SecurityError("During the processing of Action a security issue occurred preventing receiver from completing the Action successfully"),
    TypeConstraintViolation("Payload for Action is syntactically correct but at least one of the fields violates data type constraints (e.g. 'somestring': 12)"),
    ;

    override fun getName(): String {
        return name
    }

    override fun getMessage(): String {
        return message
    }
}
