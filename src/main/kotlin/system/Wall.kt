package system

import engine.model.Particle
import engine.model.Vector
import kotlin.math.absoluteValue

class Wall(
    val point: Vector,
    val normal: Vector,
    val width: Double,    // El ancho de la pared (en la dirección perpendicular a la normal)
    val height: Double,    // La altura de la pared (en la dirección perpendicular a la normal),
    val identifier: String = ""
) {
    val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun overlapsWith(particlePosition: Vector, particleRadius: Double, particle: Particle? = null): Boolean {
        if (particle != null && identifier == "BOTTOM") {
            System.out.println("")
        }
        val relativePosition = particlePosition - point
        val distanceFromWall = relativePosition.dotProduct(normal)
        return distanceFromWall < particleRadius
    }

    override fun toString(): String {
        return "Wall(identifier='$identifier')"
    }
}