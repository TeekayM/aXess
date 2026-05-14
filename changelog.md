# v0.3.3
**Changes:**
- Added config option `max_reader_powered_ticks` (default 300): changes the maximum amount of ticks you can set for a PULSE reader.

# v0.3.2
**Fixes:**
- Fixed aXess breaking other keycard models from other mods

# v0.3.1

**Fixes:**
- Fixed network editor text boxes not accepting input after deleting levels in that position
- Fixed the reader and keycard screens flickering from far away


# v0.3.0

**Changes:**
- **Texture and model overhaul**
- **Added permission system** - You can now let your friends edit the network, with granular control over what permissions they have!
- **Linking system overhaul** - You can now link multiple things together, freely!
- **Crafting recipe changes** (now uses diamonds, quartz and amethyst)
- Added operator command: `/axess`
  - `/axess version` - View mod version
  - `/axess network <delete/list/info>` - Network management
  - `/axess dump` - Dump all network data in json format
- Made receiver be able to be placed on all sides, powering the block under it
- Made reader / receiver links prettier
- Added SFX to the linker
- Made keycard readers not break when the supporting block is broken

**Fixes:**
- Fixed UI issue in the keycard reader overrides menu where buttons would go out of frame
- Fixed receiver emitting too much light
- Removed leftover useless debug messages
- Fixed reader sounds actually playing from the center of the block instead of from the bounding box
- Fixed collision boxes with readers being incorrect in certain situations
- Fixed sliders in menus not being able to hold and drag
- Fixed pulse duration ticks slider accepting odd values
- Fixed receiver not updating when linking or clearing links
- Fixed a bug where reader would still show powered state when the network was deleted
- Fixed keycard editor not updating for other players when inserting/removing a keycard

# v0.2.1

**Changes:**
- Added a recipe for the keycard (oops)
- Added zh_cn translation by [InvLabTech](https://github.com/InvLabTech)

# v0.2.0

**Changes:**
- Keycard Readers now accept "override levels". You can put access levels from other networks to be accepted regardless of the reader's settings
- Added a new type of keycard reader: the Mini Keycard Reader
- Added an "icon override" function to the keycard reader
- Added new icons
- Added separate max networks and max access levels per network config options for server operators

**Fixes:**
- Fixed the confirm button in the color and icon selection screen showing "gui.axess.button.confirm"
- Fixed an issue where the client would crash on a dedicated server when dropping a keycard
- Fixed the regular keycard reader model not having screws
- Fixed the regular keycard reader having inverted lighting under certain conditions