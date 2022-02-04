# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.0.0-1.18.1] - 2022-02-04
- Ported to Minecraft 1.18
- Internal structure has changed quite a bit, so old configs aren't compatible
### Added
- Added more options for finer control over attributes on tooltips, most notably you can now restore the look of said attributes display from Minecraft 1.8
- Added various types of food mechanics to choose from, including from the latest combat test snapshots, the current one, from before Minecraft 1.9 and a custom one
- Added an option to make the player not stop sprinting when attacking, which is mainly useful while swimming (since swimming essentially is sprinting underwater)
- Made knockback behave as it did in minecraft 1.8 again, pairs quite well with the sprinting change mentioned above
### Changed
- Blacklisting items from receiving a new damage value has been turned into overrides, so you can set your own custom attack damage value for every item
### Fixed
- Sword blocking in third-person now correctly renders the old pose from 1.7 
- Animations for block hitting/food punching/bow punching now work more reliably
### Removed
- Removed armor and elytra turning red when taking damage, this seems to no longer work in 1.18

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
