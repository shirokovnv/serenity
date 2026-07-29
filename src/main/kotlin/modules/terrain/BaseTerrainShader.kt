package modules.terrain

import graphics.assets.surface.BaseShader

abstract class BaseTerrainShader<S : BaseTerrainShader<S, M>, M : BaseTerrainMaterial<M, S>>
    : BaseShader<S, M>()