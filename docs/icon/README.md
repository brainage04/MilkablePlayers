# MilkablePlayers icon

## What this is

The mod's icon: `icon.png` — 32x32 RGBA PNG, 573 bytes,
sha256 `64db38c9df49e2c5f57213de3268f63d016f680329e3c916a0e4db586698727a`.

Two layers: behind, the vanilla **milk bucket** item texture enlarged 2x; in front, a **Steve
player head** enlarged 2x and centred.

## How it was made

**Method: generated pixel art from vanilla textures.** This is *not* an in-game screenshot and
*not* a Blender render. It is a deterministic offline composition of official Minecraft
textures with Pillow; the only pixel operations used are nearest-neighbour enlargement and alpha
compositing.

| | |
|---|---|
| Tool | Pillow 12.3.0 on Python 3.13 (`provenance/render.py`, the `MilkablePlayers` block) |
| Background source | official Java 1.21.4 `assets/minecraft/textures/item/milk_bucket.png` (16x16, sha256 `4fe54fe4…`), pinned by URL + member + sha256 in `provenance.json` |
| Background handling | doubled to 32x32 with nearest-neighbour; no trimming, no resampling |
| Foreground source | official Java 1.21.4 `assets/minecraft/textures/entity/player/wide/steve.png` (sha256 `d876e0c8…`) |
| Foreground geometry | the flat face UV `(8,8)-(16,16)` composited with the hat layer's front UV `(40,8)-(48,16)` (both flat layers, as `derivations.json` records), doubled from 8x8 to 16x16 |
| Composition | 32x32 canvas; milk bucket at `(0,0)`; Steve face centred at `(8,8)` on top |

Everything is integer, deterministic and replayable. Verified while creating this provenance:
`python3 render.py` in a clean directory holding the 17 shipped source textures reproduces
`icon.png` byte for byte.

## Provenance files

| Path | What it is |
|---|---|
| `render.py` | **The script that produced this icon.** Deterministic Pillow composition of all round-3 pixel icons (this icon's block is the `milk_bucket` + `steve` one) |
| `provenance.json` | The pinned source manifest: for every texture, the exact Mojang URL + archive member + sha256, plus the supplied skin's path and sha256 |
| `derivations.json` | The derivation record: Pillow version, the exact runtime command, and the per-texture crop/UV notes (including the player face + hat-layer rule) |
| `sources/` | The 17 source textures `render.py` reads (official Java 1.21.4 client textures, the two Alpha gear textures, and the supplied skin), each hash-verified against `provenance.json` |
| `manifest.json` | The round-3 delivery record for all pixel icons, including this one's label and method |
| `blockers.json` | The pixel session's blocker list (empty for this icon) |
| `acquire_sources.py` | Re-fetch/verify tool for `provenance.json` (see Notes for its known defect; **not needed**, every source is shipped) |

Excluded on purpose: other icons' output PNGs from the same script, the NMSR head-render helper
(`render_service.py`, `service-provenance.json`) which belongs to a different mod's icon, and
`__pycache__`.

## How to regenerate

```sh
cd provenance
PYTHONPATH=/nix/store/4v9j9wbzyhrlx9980ygbr812313mazy0-python3.13-pillow-12.3.0/lib/python3.13/site-packages \
  python3 render.py
```

This rewrites `milkable-players.png` (and the other pixel icons the script contains) and
`manifest.json`. It needs no network: every texture `render.py` reads is in `sources/`, and each
one was verified against its pinned sha256 when this provenance was assembled. `render.py`
itself never downloads anything; it only reads `sources/`.

## Notes

* The player head uses the modern 64x64 skin layout of Java 1.21.4: the flat face quad plus the
  *hat layer's* front quad, composited so that skins with a hat/overlay render the same way the
  game draws them. Both are flat 2D UV quads — this is a pixel icon, not a 3D head render.
* The milk bucket is used exactly as it ships; nothing is cropped or tinted.
* The sources are official Mojang Java 1.21.4 client textures, extracted from the pinned
  `client.jar` object recorded in `provenance.json`; they are not re-drawn by hand.
* `acquire_sources.py` has a defect inherited from the round-3 session: `provenance.json` lists
  `potion.png` twice (once as `item/potion.png`, once as `gui/sprites/container/slot/potion.png`),
  so on the second entry the already-written file fails the checksum comparison and the script
  raises after writing 13 of its 17 files. `render.py` needs the file, so it is shipped here
  verified against the `item/potion.png` pin.
* All colours and pixels come from those sources; nothing is interpolated, so the icon is crisp
  at 32x32 and at integer multiples of it.

## Working-tree note

The round-3 working tree that produced this icon was cleaned up after integration. Every file needed to regenerate the icon was copied into `provenance/`; the copies live under `provenance/from-round3/` when they came from the working tree. Any remaining `round3/...` mention records where something came from, not a path that still exists.
