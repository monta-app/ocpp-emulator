package com.monta.ocpp.emulator.ocpp.v21.protocol.error

import com.monta.library.ocpp.common.error.OcppErrorCode
import com.monta.library.ocpp.common.error.OcppErrorResponder

object OcppErrorResponderV21 : OcppErrorResponder {
    override fun getInternalError(): OcppErrorCode {
        return MessageErrorCodeV21.InternalError
    }

    override fun getJsonFormatError(): OcppErrorCode {
        return MessageErrorCodeV21.FormatViolation
    }

    override fun getPropertyConstraintViolationError(): OcppErrorCode {
        return MessageErrorCodeV21.PropertyConstraintViolation
    }

    override fun getNotImplementedError(): OcppErrorCode {
        return MessageErrorCodeV21.NotImplemented
    }
}
