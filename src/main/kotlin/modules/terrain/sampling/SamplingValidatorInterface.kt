package modules.terrain.sampling

import core.math.Vector2

interface SamplingValidatorInterface {
    fun validate(point: Vector2): Boolean
}