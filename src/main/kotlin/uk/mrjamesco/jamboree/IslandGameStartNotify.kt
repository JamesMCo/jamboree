package uk.mrjamesco.jamboree

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import uk.mrjamesco.jamboree.Jamboree.Companion.logger
import uk.mrjamesco.jamboree.Util.requestAttentionIfNotActive
import uk.mrjamesco.jamboree.integration.NoxesiumIntegration
import uk.mrjamesco.jamboree.integration.NoxesiumIntegration.onClientboundMccServerPacket

object IslandGameStartNotify {
    fun registerListeners() {
        if (!FabricLoader.getInstance().isModLoaded("noxesium")) {
            logger.info("Not registering IslandGameStartNotify listeners, as Noxesium is not loaded")
            return
        }

        logger.info("Registering IslandGameStartNotify listeners")

        NoxesiumIntegration.onClientboundMccServerPacket noxesiumPacket@{ packet ->
            // Only consider scenarios where the config option is enabled,
            // and we're joining an Island server that is of the server type "game" or "dojo"
            if (!Config.IslandGameStartNotify.enabled || !Util.onMCCIsland || (packet.server != "game" && packet.server != "dojo")) {
                return@noxesiumPacket
            }

            // We're joining a game server
            Minecraft.getInstance().window.requestAttentionIfNotActive()
        }
    }
}
