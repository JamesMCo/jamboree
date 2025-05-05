package uk.mrjamesco.jamboree

import com.mojang.blaze3d.platform.Window
import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object IslandGameStartNotify {
    internal val Window.isActive: Boolean
        get() = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) != 0

    internal fun Window.requestAttention() = GLFW.glfwRequestWindowAttention(window)

    fun registerListeners() {
        if (!FabricLoader.getInstance().isModLoaded("noxesium")) {
            logger.info("Not registering IslandGameStartNotify listeners, as Noxesium is not loaded")
            return
        }

        logger.info("Registering IslandGameStartNotify listeners")

        NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) noxesiumPacket@{ _, packet, _ ->
            // Only consider scenarios where the config option is enabled,
            // and we're joining an Island server with an associated game
            if (!Config.IslandGameStartNotify.enabled || !Util.onMCCIsland || packet.associatedGame.isEmpty()) {
                return@noxesiumPacket
            }

            // We're joining a game server (doesn't count fishing, as those are classified as lobby servers)
            Minecraft.getInstance().window.run {
                if (!isActive) {
                    requestAttention()
                }
            }
        }
    }
}
