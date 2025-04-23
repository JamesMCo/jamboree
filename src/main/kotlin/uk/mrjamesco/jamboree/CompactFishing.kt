package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.NoxesiumMod
import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object CompactFishing {
    fun altIconOrder(iconText: String): Int = when (iconText) {
        // Order suggested by Dwittyy
        // https://github.com/JamesMCo/jamboree/issues/1

        "Elusive Catch" -> 0

        "XP Magnet" -> 1
        "Fish Magnet" -> 2
        "Pearl Magnet" -> 3
        "Treasure Magnet" -> 4
        "Spirit Magnet" -> 5

        "Boosted Rod" -> 6
        "Speedy Rod" -> 7
        "Graceful Rod" -> 8
        "Glitched Rod" -> 9
        "Stable Rod" -> 10

        "Supply Preserve" -> 11

        else -> Int.MAX_VALUE
    }

    private var onMCCIsland: Boolean = false
    private var onFishingIsland: Boolean = false

    private var caughtMessage: MutableComponent? = null
    private val iconBuffer: MutableList<Pair<MutableComponent, Int>> = mutableListOf()
    private var xpMessage: MutableComponent? = null
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
        NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) { _, packet, _ -> onFishingIsland = (packet.serverType == "lobby" && Regex("^(temperate|tropical|barren)_.+").matches(packet.subType)) }

        // Detect fishing messages
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            if (flushingBuffer) {
                return@register true
            }

            if (Config.CompactFishing.enabled && onMCCIsland && onFishingIsland) {
                if (Regex("^\\(.\\) You caught: \\[.+].*").matches(message.string)) {
                    caughtMessage = message.copy()
                    return@register false
                } else if (Regex("^\\s*. (Triggered|Special): .+").matches(message.string)) {
                    if (Config.CompactFishing.showIcons) {
                        message
                            .siblings.first() // e.g. "[] Triggered: [] Supply Preserve"
                            .siblings.last()  // e.g. "Triggered: [] Supply Preserve"
                            .siblings         // e.g. "[]", " ", "Supply Preserve"
                            .let {
                                iconBuffer.addLast(Pair(
                                    it.first().copy().apply { style = message.siblings.first().style },
                                    altIconOrder(it.last().string)
                                ))
                            }
                    }
                    return@register false
                } else if (Regex("^\\s*. You earned: .+").matches(message.string)) {
                    if (Config.CompactFishing.showXP) {
                        message
                            .siblings.last()  // "You earned: n Island XP"
                            .siblings.last()  // "n Island XP"
                            .let {
                                xpMessage = it.copy()
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
            caughtMessage!!.apply {
                if (iconBuffer.isNotEmpty()) {
                    append(Component.literal(" "))

                    if (Config.CompactFishing.useAltIconOrder) {
                        iconBuffer.sortedBy { it.second }
                    } else {
                        iconBuffer
                    }.forEach { append(it.first) }
                }

                if (xpMessage != null) {
                    append(Component.literal(" +"))
                    append(xpMessage!!)
                }
            }, false
        )

        caughtMessage = null
        iconBuffer.clear()
        xpMessage = null

        flushingBuffer = false
    }
}
