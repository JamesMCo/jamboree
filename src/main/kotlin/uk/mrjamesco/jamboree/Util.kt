package uk.mrjamesco.jamboree

import com.mojang.blaze3d.platform.Window
import org.lwjgl.glfw.GLFW
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object Util {
    fun registerListeners() {
        logger.info("Registering Util listeners")
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
