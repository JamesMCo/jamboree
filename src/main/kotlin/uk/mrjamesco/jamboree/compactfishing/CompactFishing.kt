package uk.mrjamesco.jamboree.compactfishing

import com.noxcrew.noxesium.NoxesiumFabricMod
import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import uk.mrjamesco.jamboree.Config
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

    internal fun Component.isCaughtMessage(): Boolean = Regex("^\\(.\\) You caught: \\[.+].*").matches(this.string)
    internal fun Component.isIconMessage(): Boolean   = Regex("^\\s*. (Triggered|Special): .+").matches(this.string)
    internal fun Component.isXPMessage(): Boolean     = Regex("^\\s*. You earned: .+").matches(this.string)

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
        ClientReceiveMessageEvents.ALLOW_GAME.register allowMessage@{ message, _ ->
            if (Config.CompactFishing.enabled && onMCCIsland && onFishingIsland) {
                if (message.isCaughtMessage()) {
                    return@allowMessage Config.CompactFishing.mode.handleCaughtMessage(message)
                } else if (message.isIconMessage()) {
                    return@allowMessage Config.CompactFishing.mode.handleIconMessage(message)
                } else if (message.isXPMessage()) {
                    return@allowMessage Config.CompactFishing.mode.handleXPMessage(message)
                }
            }

            // This isn't a message relating to catching a fish
            return@allowMessage true
        }
    }
}
