package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.NoxesiumFabricMod
import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object CompactFishing {
    private var onMCCIsland: Boolean = false
    private var onFishingIsland: Boolean = false

    private val messageBuffer: MutableList<MutableComponent> = mutableListOf()
    private var flushingBuffer: Boolean = false

    fun registerListeners() {
        if (!FabricLoader.getInstance().isModLoaded("noxesium")) {
            logger.info("Not registering CompactFishing listeners, as Noxesium is not loaded")
            return
        }

        logger.info("Registering CompactFishing listeners")

        // Detect joining MCC Island
        ClientPlayConnectionEvents.JOIN.register { handler, _, _ -> onMCCIsland = Regex("mccisland\\.(net|com)").containsMatchIn(handler.connection.remoteAddress.toString()) }

        // Detect being in a fishing server
        NoxesiumFabricMod.initialize()
        NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) { _, packet, _ -> onFishingIsland = (packet.serverType == "lobby" && Regex("^(temperate|tropical|barren)_.+").matches(packet.subType)) }

        // Detect fishing messages
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            if (flushingBuffer) {
                return@register true
            }

            if (Config.CompactFishing.enabled && onMCCIsland && onFishingIsland) {
                if (Regex("^\\(.\\) You caught: \\[.+].*").matches(message.string)) {
                    messageBuffer.addLast(message.copy())
                    return@register false
                } else if (Regex("^\\s*. (Triggered|Special): .+").matches(message.string)) {
                    if (Config.CompactFishing.showIcons) {
                        if (messageBuffer.size == 1) {
                            // Add space before first perk icon
                            messageBuffer.addLast(Component.literal(" "))
                        }
                        message
                            .siblings.first() // e.g. "[] Triggered: [] Supply Preserve"
                            .siblings.last()  // e.g. "Triggered: [] Supply Preserve"
                            .siblings.first() // e.g. Supply preserve icon
                            .let {
                                messageBuffer.addLast(it.copy().apply { style = message.siblings.first().style })
                            }
                    }
                    return@register false
                } else if (Regex("^\\s*. You earned: .+").matches(message.string)) {
                    if (Config.CompactFishing.showXP) {
                        message
                            .siblings.last()  // "You earned: n Island XP"
                            .siblings.last()  // "n Island XP"
                            .let {
                                messageBuffer.addLast(Component.literal(" +").apply { style = it.style })
                                messageBuffer.addLast(it.copy())
                            }
                    }
                    flushMessageBuffer()
                    return@register false
                }
            }

            return@register true
        }
    }

    fun flushMessageBuffer() {
        flushingBuffer = true
        Minecraft.getInstance().player?.displayClientMessage(
            messageBuffer.fold(Component.empty(), MutableComponent::append),
            false
        )
        flushingBuffer = false

        messageBuffer.clear()
    }
}
