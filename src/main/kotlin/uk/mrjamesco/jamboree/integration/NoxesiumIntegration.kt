package uk.mrjamesco.jamboree.integration

import com.noxcrew.noxesium.network.NoxesiumPackets
import com.noxcrew.noxesium.network.clientbound.ClientboundMccServerPacket

object NoxesiumIntegration {
    fun Any.onClientboundMccServerPacket(f: (ClientboundMccServerPacket) -> Unit) =
        NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) { _, packet, _ -> f(packet) }
}
