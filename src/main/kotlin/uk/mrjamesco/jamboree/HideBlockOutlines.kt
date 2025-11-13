package uk.mrjamesco.jamboree

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.Minecraft
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object HideBlockOutlines {
    fun registerListeners() {
        logger.info("Registering HideBlockOutlines listeners")
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register { _, _ ->
            // Show outlines if not both: Hide Block Outlines config setting is enabled and currently in creative mode
            // If either of those is not true, then show outlines
            !(Config.AdjustRendering.hideBlockOutlines && (Minecraft.getInstance().gameMode?.playerMode?.isCreative ?: false))
        }
    }
}
