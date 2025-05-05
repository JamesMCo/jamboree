package uk.mrjamesco.jamboree

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object Util {
    var onMCCIsland: Boolean = false
        private set

    fun registerListeners() {
        logger.info("Registering Util listeners")

        // Detect joining MCC Island
        ClientPlayConnectionEvents.JOIN.register { handler, _, _ -> onMCCIsland = Regex("mccisland\\.(net|com)").containsMatchIn(handler.connection.remoteAddress.toString()) }
    }
}
