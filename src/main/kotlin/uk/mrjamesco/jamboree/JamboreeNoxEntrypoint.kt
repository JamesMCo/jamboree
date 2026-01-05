package uk.mrjamesco.jamboree

import com.noxcrew.noxesium.core.fabric.mcc.MccNoxesiumEntrypoint
import uk.mrjamesco.jamboree.compactfishing.CompactFishing

class JamboreeNoxEntrypoint : MccNoxesiumEntrypoint() {
    override fun initialize() {
        IslandGameStartNotify.registerListeners()
        CompactFishing.registerListeners()
    }
}