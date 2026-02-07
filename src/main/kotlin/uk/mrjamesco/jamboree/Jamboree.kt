package uk.mrjamesco.jamboree

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import uk.mrjamesco.jamboree.compactfishing.CompactFishing

class Jamboree: ModInitializer {
    override fun onInitialize() {
        logger.info("Initialising Jamboree v${FabricLoader.getInstance().getModContainer("jamboree").get().metadata.version}")
        Config.init()
        Util.registerListeners()
        ChatDing.registerListeners()
        CompactFishing.registerListeners()
        HideBlockOutlines.registerListeners()
        IslandGameStartNotify.registerListeners()
    }

    companion object {
        val logger: Logger = PrefixedLogger("Jamboree")
    }
}
