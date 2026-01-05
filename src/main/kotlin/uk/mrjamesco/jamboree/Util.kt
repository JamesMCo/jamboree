package uk.mrjamesco.jamboree

import com.mojang.blaze3d.platform.Window
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.lwjgl.glfw.GLFW
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object Util {
    var onMCCIsland: Boolean = false
        private set

    fun registerListeners() {
        logger.info("Registering Util listeners")

        // Detect joining MCC Island
        ClientPlayConnectionEvents.JOIN.register { handler, _, _ -> onMCCIsland = Regex("mccisland\\.(net|com)").containsMatchIn(handler.connection.remoteAddress.toString()) }
    }

    val Window.isActive: Boolean
        get() = GLFW.glfwGetWindowAttrib(handle(), GLFW.GLFW_FOCUSED) != 0

    fun Window.requestAttention() = GLFW.glfwRequestWindowAttention(handle())

    fun Window.requestAttentionIfNotActive() {
        if (!isActive) {
            requestAttention()
        }
    }
}
