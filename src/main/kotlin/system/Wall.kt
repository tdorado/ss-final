package system

import engine.model.Particle
import engine.model.Vector
import kotlin.math.abs
import kotlin.math.absoluteValue

class Wall(
    val position: Vector,
    val normal: Vector,
    val Kn: Double,
    val Kt: Double,
    val gammaN: Double,
    val gammaT: Double,
    val id: String
) {
    val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun overlapsWithParticle(positionToCompare: Vector, radius: Double): Boolean {
        val relativePosition = positionToCompare - position
        val distanceFromWall = relativePosition.dotProduct(normal)
        return distanceFromWall <= radius
    }

    fun isParticleInsideBox(particle: Particle, boxWidth: Double, boxHeight: Double): Boolean {
        if (particle.id == 0 && id == "BOTTOM"){
            System.out.println("")
        }
        val position = particle.position
        return !(abs(position.x) > (boxWidth/2) || abs(position.y) > (boxWidth) || abs(position.z) > boxHeight)
    }

    fun isOutsideBox(position: Vector): Boolean {
        val wallToPosition = position - this.position
        return wallToPosition.dotProduct(this.normal) < 0
    }

    override fun toString(): String {
        return "Wall(identifier='$id')"
    }
}