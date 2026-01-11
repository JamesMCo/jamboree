package uk.mrjamesco.jamboree.integration

import com.noxcrew.noxesium.core.fabric.mcc.MccNoxesiumEntrypoint
import com.noxcrew.noxesium.core.mcc.ClientboundMccServerPacket
import com.noxcrew.noxesium.core.mcc.MccPackets

object NoxesiumIntegration : MccNoxesiumEntrypoint() {
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

    fun Any.onClientboundMccServerPacket(f: (ClientboundMccServerPacket) -> Unit) = whenInitialized {
        MccPackets.CLIENTBOUND_MCC_SERVER.addListener(this, ClientboundMccServerPacket::class.java) { _, packet, _ -> f(packet) }
    }
}
