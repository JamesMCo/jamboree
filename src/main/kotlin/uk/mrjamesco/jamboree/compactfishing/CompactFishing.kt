package uk.mrjamesco.jamboree.compactfishing

import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.GuiMessage
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Config
import uk.mrjamesco.jamboree.Jamboree.Companion.logger
import uk.mrjamesco.jamboree.mixins.ChatComponentMixin

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
    private var sendingCompactMessage: Boolean = false

    private fun Component.isCaughtMessage(): Boolean = Regex("^\\(.\\) You caught: \\[.+].*").matches(this.string)
    private fun Component.isIconMessage(): Boolean   = Regex("^\\s*. (Triggered|Special): .+").matches(this.string)
    private fun Component.isXPMessage(): Boolean     = Regex("^\\s*. You earned: .+").matches(this.string)

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
        ClientReceiveMessageEvents.ALLOW_GAME.register allowMessage@{ message, _ ->
            if (sendingCompactMessage) {
                return@allowMessage true
            }

            if (Config.CompactFishing.enabled && onMCCIsland && onFishingIsland) {
                if (message.isCaughtMessage()) {
                    caughtMessage = message.copy()

                    // Seeing a new caught message means we are handling a new set of messages
                    // Therefore, we need to clear the icons and xp message
                    iconBuffer.clear()
                    xpMessage = null

                    // If collecting messages, then need to block the message
                    // Otherwise, need to let the message through
                    return@allowMessage !Config.CompactFishing.collect
                } else if (message.isIconMessage()) {
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

                        if (!Config.CompactFishing.collect) {
                            // If not collecting messages, then we need to update chat now to show the new icon
                            updateChat()
                        }
                    }
                    return@allowMessage false
                } else if (message.isXPMessage()) {
                    if (Config.CompactFishing.showXP) {
                        message
                            .siblings.last()  // "You earned: n Island XP"
                            .siblings.last()  // "n Island XP"
                            .let {
                                xpMessage = it.copy()
                            }
                        // If showing xp, then we need to update chat (whether collecting or not)
                        updateChat()
                    } else if (Config.CompactFishing.collect) {
                        // If not showing xp, then we only need to update chat if collecting messages
                        // (as icons will already be present in the chat log)
                        updateChat()
                    }
                    return@allowMessage false
                }
            }

            // This isn't a message relating to catching a fish
            return@allowMessage true
        }
    }

    fun buildCompactMessage(): Component = caughtMessage?.copy()?.apply {
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
    } ?: Component.empty()

    fun updateChat() {
        if (Config.CompactFishing.collect) {
            // Update chat by sending a new message
            sendingCompactMessage = true
            Minecraft.getInstance().player?.displayClientMessage(buildCompactMessage(), false)
            sendingCompactMessage = false
        } else {
            // Update chat by replacing an existing message

            // Only need to toggle sendingCompactMessage when sending a new message
            // (replacing an existing message doesn't trigger ClientReceiveMessageEvents.ALLOW_GAME)
            (Minecraft.getInstance().gui.chat as ChatComponentMixin).apply replaceExistingCatchMessage@{
                allMessages.forEachIndexed { i, message ->
                    if (message.content.isCaughtMessage()) {
                        // Compact messages start with caught messages, so will match the same regex
                        allMessages[i] = GuiMessage(
                            message.addedTime,
                            buildCompactMessage(),
                            message.signature,
                            message.tag
                        )
                        refreshTrimmedMessages()
                        return@replaceExistingCatchMessage
                    }
                }
            }
        }
    }
}
