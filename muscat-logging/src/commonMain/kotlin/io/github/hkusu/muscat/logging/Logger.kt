package io.github.hkusu.muscat.logging

interface Logger {
    suspend fun log(
        severity: Severity,
        tag: String,
        throwable: Throwable?,
        message: String,
    )

    enum class Severity {
        Verbose,
        Debug,
        Info,
        Warn,
        Error,
        Assert,
    }
}
