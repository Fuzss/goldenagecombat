# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.0.0-1.18.1] - 2022-02-07
- Ported to Minecraft 1.18
- In addition to all the old combat from Minecraft 1.8 and before, the mod now also contains a few features from combat test snapshots, and more should follow in the future
- So this is basically going to turn into a reimplementation of most combat snapshot features, but on top of 1.8 combat instead 1.9 combat
- But of course the config is very extensive, and you always have the option to disable everything you don't like, so playing pure 1.8 combat is still possible, but you now also have the option to craft your very own combat system
### Added
- Made it possible to hit targets through blocks without a collision box such as tall grass
- Holding down the attack key now continues attacking without the need to spam click (although spam clicking remains possible and may be more effective, this option was purely added to safe your hands from arthritis :))
- Added more options for finer control over attributes on tooltips, most notably you can now restore the look of said attributes display from Minecraft 1.8
- Added various types of food mechanics to choose from, including from the latest combat test snapshots, the current one, from before Minecraft 1.9 and a custom one
- Added an option to make the player not stop sprinting when attacking, which is mainly useful while swimming (since swimming essentially is sprinting underwater)
- Made knockback behave as it did in Minecraft 1.8 again, pairs quite well with the sprinting change mentioned above
- Added option to use the item attack animation from combat test snapshots
- Added a few more options for controlling the sweep attack
### Changed
- Blacklisting items from receiving a new damage value has been turned into overrides, so you can set your own custom attack damage value for every item
### Fixed
- Sword blocking in third-person now correctly renders the old pose from 1.7 
- Animations for block hitting/food punching/bow punching now work more reliably
- Fixed option for disabling sweeping attacks from triggering without the enchantment also affecting player movement
### Removed
- Removed armor and elytra turning red when taking damage, this seems to no longer work in 1.18
- Removed Notch Apple (enchanted golden apple) recipe since it cannot be disabled, if you want it back use a data pack

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
