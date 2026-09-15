package uk.mrjamesco.jamboree

import com.mojang.blaze3d.platform.Window
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.lwjgl.sdl.SDLVideo
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
        get() = (SDLVideo.SDL_GetWindowFlags(handle()) and SDLVideo.SDL_WINDOW_INPUT_FOCUS) != 0L

    fun Window.requestAttention() = SDLVideo.SDL_FlashWindow(handle(), SDLVideo.SDL_FLASH_BRIEFLY)

    fun Window.requestAttentionIfNotActive() {
        if (!isActive) {
            requestAttention()
        }
    }
}
