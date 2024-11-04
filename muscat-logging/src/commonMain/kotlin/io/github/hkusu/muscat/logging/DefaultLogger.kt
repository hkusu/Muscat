package io.github.hkusu.muscat.logging

import co.touchlab.kermit.Severity
import co.touchlab.kermit.Logger.Companion as Kermit

internal class DefaultLogger : Logger {
    override suspend fun log(severity: Logger.Severity, tag: String, throwable: Throwable?, message: String) {
        Kermit.log(
            severity = severity.map(),
            tag = tag,
            throwable = throwable,
            message = message,
        )
    }

    private fun Logger.Severity.map() = when (this) {
        Logger.Severity.Verbose -> Severity.Verbose
        Logger.Severity.Debug -> Severity.Debug
        Logger.Severity.Info -> Severity.Info
        Logger.Severity.Warn -> Severity.Warn
        Logger.Severity.Error -> Severity.Error
        Logger.Severity.Assert -> Severity.Assert
    }
}
