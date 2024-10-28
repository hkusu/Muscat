package io.github.hkusu.muscat.logging

interface Logger {
    fun log(
        level: Level = Level.Debug,
        tag: String = "Tart",
        message: () -> String,
    )

    enum class Level {
        Verbose,
        Debug,
        Info,
        Warn,
        Error,
        Assert
    }
}
