package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.core.mcc.ClientboundMccServerPacket
import com.noxcrew.noxesium.core.mcc.MccPackets
import net.minecraft.client.Minecraft
import uk.mrjamesco.jamboree.Jamboree.Companion.logger
import uk.mrjamesco.jamboree.Util.requestAttentionIfNotActive

object IslandGameStartNotify {
    fun registerListeners() {
        logger.info("Registering IslandGameStartNotify listeners")

        MccPackets.CLIENTBOUND_MCC_SERVER.addListener(this, ClientboundMccServerPacket::class.java) noxesiumPacket@{ _, packet, _ ->
            // Only consider scenarios where the config option is enabled,
            // we're joining an Island server that isn't a lobby
            if (!Config.IslandGameStartNotify.enabled || !Util.onMCCIsland || packet.server == "lobby" || packet.server == "fishing") {
                return@noxesiumPacket
            }

            // We're joining a game server (doesn't count fishing, as those are classified as lobby servers)
            Minecraft.getInstance().window.requestAttentionIfNotActive()
        }
    }
}
