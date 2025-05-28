# Change Log

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

### Other Notes & Contributions


## [0.0.3] - 2025-05-28

### Added

* Wasm JS is now officially supported and artifacts are published.

### Changed

- Snapshots are now published to the Central Portal Snapshots repository at https://central.sonatype.com/repository/maven-snapshots/.
- Upgraded Kotlin to `2.1.21`.

### Removed

- Removed the deprecated `onEvent` function used in `MoleculePresenters`. This is no longer needed since Kotlin 2.0.20, see #21.


## [0.0.2] - 2025-05-02

### Changed

- **Breaking change:** Changed the constructor from `ComposeAndroidRendererFactory` to two factory functions instead. A new API allows you to use this factory without an Android View as parent, see #39.

### Deprecated

- Deprecated the `onEvent` function used in `MoleculePresenters`. This is no longer needed since Kotlin 2.0.20, see #21.

### Fixed

- Made the `ModuleStructureDependencyCheckTask` cacheable, see #19.
- Fixed violations for Gradle's project isolation feature, see #20.

### Other Notes

- Updated the sample application with a shared transition animation to highlight how animations can be implemented for `Template` updates, see #37.


## [0.0.1] - 2025-04-17

- Initial release.

[Unreleased]: https://github.com/amzn/app-platform/compare/0.0.3...HEAD
[0.0.3]: https://github.com/amzn/app-platform/compare/0.0.3
[0.0.2]: https://github.com/amzn/app-platform/compare/0.0.2
[0.0.1]: https://github.com/amzn/app-platform/compare/0.0.1
