# Jamboree

A Minecraft Fabric mod containing miscellaneous utility stuff (including for MCC Island)

**Requires Minecraft 1.21 or higher, [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin),
and [Yet Another Config Lib](https://modrinth.com/mod/yacl).
[Mod Menu](https://modrinth.com/mod/modmenu) is an optional dependency
(though I'd recommend it unless you are particularly fond of editing
config JSON files by hand).**  
_(Some features require [Noxesium](https://modrinth.com/mod/noxesium) - see below)_

- 🫙 General Features
  - Chat Ding: play a sound when certain phrases or words appear in messages in chat
  - Adjust Rendering: various toggles to be able to adjust the rendering of the game
      - Hide Block Outlines: hide block outlines when the player is in creative mode (useful for recording from an external perspective in creative mode when you don't want to be able to see block outlines, but do want to be able to see the rest of the UI)
      - Hide Scoreboard: hide scoreboard when the player is in creative mode (useful for recording in creative mode when you don't want to be able to see the scoreboard, but do want to be able to see the rest of the UI)
- 🏝️ MCC Island Features
  - Compact Fishing: combine multiple messages shown when catching a fish on MCC Island in to one message (requires Noxesium, compatible with [MCC Fishing Messages](https://modrinth.com/mod/mcc-fishing-messages-mod) mod)
  - Island Game Start Notify: cause the window icon to flash when tabbed out while joining a game server on MCC Island, such as when a game queue has filled (requires Noxesium)

---

For the curious, here are the changes that necessitate releases for different versions of Minecraft:

- **`1.21-1.21.8` -> `1.21.9`**
  - `com.mojang.blaze3d.platform.Window.getWindow()` is renamed to `com.mojang.blaze3d.platform.Window.getHandle()`
  - `net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents` is removed
    - This API is reintroduced in a new form in `1.21.10` as `net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents`, but isn't used in this project until another change justifies maintaining a new version branch
- **`1.21.9-1.21.10` -> `1.21.11`**
  - Support is added for MCCI functionality
- **`1.21.11` -> `26.1`**
  - Minecraft jar is deobfuscated
  - `net.minecraft.client.GuiMessage` is moved to `net.minecraft.client.multiplayer.chat.GuiMessage`
  - `net.minecraft.client.player.LocalPlayer.displayClientMessage()` is split into two, with this project using `net.minecraft.client.player.LocalPlayer.sendSystemMessage()`
  - `net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents` is renamed to `net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents`
- **`26.1` -> `26.2`**
  - `net.minecraft.client.gui.Gui.getChat()` is moved to `net.minecraft.client.gui.Hud.getChat()`
  - `net.minecraft.client.gui.Gui.displayScoreboardSidebar()` is moved to `net.minecraft.client.gui.Hud.displayScoreboardSidebar()`
