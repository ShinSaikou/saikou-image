# Changelog

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and versions follow
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-10-30

### Added

- `ImageUtil`: format sniffing from magic bytes (JPEG, PNG, WebP, GIF, AVIF, HEIF, JPEG XL),
  animation detection, wide-page detection, splitting a double page in half or into a stacked
  pair, rotation, reader background detection, and hardware bitmap checks with a list of devices
  whose hardware bitmaps are broken.
- `GLUtil`: the device's maximum texture size, and the options a reader can offer as a custom
  limit.
- `TallImageSplitCalculator`: when a tall strip should be split, and into how many parts.

The code was moved out of the Saikou app, where it was used by the reader and the download engine.

[Unreleased]: https://github.com/ShinSaikou/saikou-image/compare/0.1.0...HEAD
[0.1.0]: https://github.com/ShinSaikou/saikou-image/releases/tag/0.1.0
