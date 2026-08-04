package modules.terrain.roam.buffers

import graphics.assets.Asset
import graphics.rendering.Drawable

interface PatchBufferInterface : Asset, Drawable {
    val type: PatchBufferType
}
