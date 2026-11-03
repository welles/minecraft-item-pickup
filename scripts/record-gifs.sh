#!/usr/bin/env bash
# Plays the scenes of src/gametest in a headless Minecraft and turns them into GIFs in media/.
# Run it in the Nix shell, which provides xvfb-run and ffmpeg: nix-shell --run scripts/record-gifs.sh
set -euo pipefail
cd "$(dirname "$0")/.."

node=1.21.11-fabric
frames=versions/$node/build/run/clientGameTest/recordings

# A virtual display with Mesa's software renderer, so no window opens; the game test clears its run directory first
LIBGL_ALWAYS_SOFTWARE=1 __GLX_VENDOR_LIBRARY_NAME=mesa xvfb-run -a -s '-screen 0 1280x720x24' \
    ./gradlew ":$node:runClientGameTest"

mkdir -p media
for scene in "$frames"/*/; do
    name=$(basename "$scene")
    # one frame per tick, 480 pixels wide, a palette of its own and no dithering keep the GIFs small
    ffmpeg -loglevel error -y -framerate 20 -i "$scene/%04d.png" \
        -vf 'scale=480:-1:flags=lanczos,split[a][b];[a]palettegen=max_colors=128:stats_mode=full[p];[b][p]paletteuse=dither=none:diff_mode=rectangle' \
        -loop 0 "media/$name.gif"
    echo "media/$name.gif: $(du -h "media/$name.gif" | cut -f1)"
done
