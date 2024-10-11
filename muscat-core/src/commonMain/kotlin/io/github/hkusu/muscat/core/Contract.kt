package io.github.hkusu.muscat.core

sealed interface Contract
interface State : Contract
interface Action : Contract
interface Event : Contract