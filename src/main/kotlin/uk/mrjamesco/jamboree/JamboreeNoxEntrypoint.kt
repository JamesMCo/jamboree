package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.core.fabric.mcc.MccNoxesiumEntrypoint

object JamboreeNoxEntrypoint : MccNoxesiumEntrypoint() {
    private var initialized: Boolean = false

    private val waitingFuncs: MutableList<() -> Unit> = mutableListOf()

    override fun initialize() {
        initialized = true
        waitingFuncs.forEach { it() }
        waitingFuncs.clear()
    }

    fun whenInitialized(f: () -> Unit) {
        when (initialized) {
            true -> f()
            false -> waitingFuncs.add(f)
        }
    }
}
