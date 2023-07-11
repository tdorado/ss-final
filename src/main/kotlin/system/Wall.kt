package system

import engine.model.Particle
import engine.model.Vector
import kotlin.math.abs
import kotlin.math.absoluteValue

class Wall(
    val position: Vector,
    val normal: Vector,
    val id: String
) {
    val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun overlapsWithParticle(positionToCompare: Vector, radius: Double, boxWidth: Double, boxHeight: Double): Boolean {
        val relativePosition = positionToCompare - position
        val distanceFromWall = relativePosition.dotProduct(normal)

        val insideBox =
            positionToCompare.x in 0.0..boxWidth &&
                    positionToCompare.y >= 0 && positionToCompare.y <= boxWidth &&
                    positionToCompare.z >= 0 && positionToCompare.z <= boxHeight

        return insideBox && distanceFromWall < radius
    }

    override fun toString(): String {
        return "Wall(identifier='$id')"
    }
}