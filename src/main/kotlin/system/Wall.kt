package system

import engine.model.Vector

class Wall(
    val position: Vector,
    val normal: Vector,
    val kn: Double,
    val kt: Double,
    val gamma: Double,
    val id: String
) {

    fun overlapsWith(particlePosition: Vector, particleRadius: Double): Boolean {
        val relativePosition = particlePosition - position
        val distanceFromWall = relativePosition.dotProduct(normal)
        return distanceFromWall < particleRadius
    }

    override fun toString(): String {
        return "Wall(id='$id')"
    }
}