package system

import engine.model.Particle
import engine.model.Vector

class Wall(
    val position: Vector,
    val normal: Vector,
    val Kn: Double,
    val Kt: Double,
    val gamma: Double,
    val id: String
) {

    fun overlapsWithParticle(particle: Particle, boxWidth: Double, boxHeight: Double): Boolean {
        val relativePosition = particle.position - this.position
        val distanceFromWall = relativePosition.dotProduct(this.normal)

        val insideBox =
            particle.position.x in 0.0..boxWidth &&
                    particle.position.y in 0.0..boxWidth &&
                    particle.position.z in 0.0..boxHeight

        return insideBox && distanceFromWall < particle.radius
    }

    fun overlapsWith(particlePosition: Vector, particleRadius: Double): Boolean {
        val relativePosition = particlePosition - position
        val distanceFromWall = relativePosition.dotProduct(normal)
        return distanceFromWall < particleRadius
    }

    override fun toString(): String {
        return "Wall(identifier='$id')"
    }
}