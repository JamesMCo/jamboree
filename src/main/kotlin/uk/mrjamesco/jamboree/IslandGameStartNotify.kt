package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.core.mcc.ClientboundMccServerPacket
import com.noxcrew.noxesium.core.mcc.MccPackets
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import uk.mrjamesco.jamboree.Jamboree.Companion.logger
import uk.mrjamesco.jamboree.Util.requestAttentionIfNotActive

object IslandGameStartNotify {
    fun registerListeners() {
        if (!FabricLoader.getInstance().isModLoaded("noxesium")) {
            logger.info("Not registering IslandGameStartNotify listeners, as Noxesium is not loaded")
            return
        }

        logger.info("Registering IslandGameStartNotify listeners")

        JamboreeNoxEntrypoint.whenInitialized {
            MccPackets.CLIENTBOUND_MCC_SERVER.addListener(this, ClientboundMccServerPacket::class.java) noxesiumPacket@{ _, packet, _ ->
                // Only consider scenarios where the config option is enabled,
                // we're joining an Island server that isn't a lobby, and
                // the server has "game" in its type
                if (!Config.IslandGameStartNotify.enabled || !Util.onMCCIsland || packet.server == "lobby" || "game" !in packet.types) {
                    return@noxesiumPacket
                }

                // We're joining a game server
                Minecraft.getInstance().window.requestAttentionIfNotActive()
            }
        }
    }
}
