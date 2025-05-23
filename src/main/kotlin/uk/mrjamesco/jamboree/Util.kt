package uk.mrjamesco.jamboree

import com.mojang.blaze3d.platform.Window
import com.noxcrew.noxesium.NoxesiumFabricMod
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.lwjgl.glfw.GLFW
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object Util {
    var onMCCIsland: Boolean = false
        private set

    fun registerListeners() {
        logger.info("Registering Util listeners")

        // Detect joining MCC Island
        ClientPlayConnectionEvents.JOIN.register { handler, _, _ -> onMCCIsland = Regex("mccisland\\.(net|com)").containsMatchIn(handler.connection.remoteAddress.toString()) }

        // Initialise Noxesium once if it's loaded
        if (FabricLoader.getInstance().isModLoaded("noxesium")) {
            NoxesiumFabricMod.initialize()
        }
    }

    val Window.isActive: Boolean
        get() = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) != 0

    fun Window.requestAttention() = GLFW.glfwRequestWindowAttention(window)

    fun Window.requestAttentionIfNotActive() {
        if (!isActive) {
            requestAttention()
        }
    }
}
