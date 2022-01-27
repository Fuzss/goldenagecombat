# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.0.0-1.18.1] - 2022-01-27
- Ported to Minecraft 1.18
- Internal structure has changed quite a bit, so old configs aren't compatible
### Added
- Added more options for finer control over attributes on tooltips, most notably you can now restore the look of said attributes display from prior to Minecraft 1.13
- Added the food mechanics from the latest combat test snapshots, although the current one and from before Minecraft 1.9 are still available to choose
- Added an option to hide shields and render a sword block instead, intended for servers that temporarily give you a shield for your offhand to mimic sword blocking
- Added an option to make the player not stop sprinting when attacking, which is mainly useful while swimming (since swimming essentially is sprinting underwater)
### Changed
- Converted some item tags to config options to allow for easier access
### Fixed
- Sword blocking in third-person now correctly renders the old pose from 1.7 
- Animations for block hitting/food punching/bow punching now work more reliably
### Removed
- Removed armor and elytra turning red when taking damage, this seems to no longer work in 1.18
- Removed option for disabling lost health from flashing, should also return soon
- Removed option for hiding damage indicator particles, will return soon

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
