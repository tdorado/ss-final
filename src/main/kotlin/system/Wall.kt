package system

import engine.model.Particle
import engine.model.Vector
import kotlin.math.absoluteValue

class Wall(
    val position: Vector,
    val normal: Vector,
    val frictionCoefficient: Double,
    val id: String
) {
    val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun overlapsWithParticle(positionToCompare: Vector, radius: Double): Boolean {
        val relativePosition = positionToCompare - position
        val distanceFromWall = relativePosition.dotProduct(normal)
        return distanceFromWall <= radius
    }

    override fun toString(): String {
        return "Wall(identifier='$id')"
    }
}