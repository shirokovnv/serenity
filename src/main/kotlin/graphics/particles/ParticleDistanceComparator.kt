package graphics.particles

import core.math.Vector3
import core.math.helpers.distanceSquared

class ParticleDistanceComparator(
    private val origin: Vector3
) : Comparator<Particle> {

    override fun compare(p1: Particle, p2: Particle): Int {
        val dist1 = distanceSquared(p1.position, origin)
        val dist2 = distanceSquared(p2.position, origin)

        if (dist1 < dist2) {
            return 1
        } else if (dist1 > dist2) {
            return -1
        }

        return 0
    }
}