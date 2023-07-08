package engine.model

import kotlin.math.absoluteValue

class Wall(
    val point: Vector,
    val normal: Vector,
    val width: Double,    // El ancho de la pared (en la dirección perpendicular a la normal)
    val height: Double    // La altura de la pared (en la dirección perpendicular a la normal)
) {
    val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun overlapsWith(particlePosition: Vector, particleRadius: Double): Boolean {
        val relativePosition = particlePosition - (point)
        val distance = relativePosition.dotProduct(normal)

        // Check if the particle is beyond the plane defined by the wall
        if (distance > particleRadius) return false

        // Check if the particle is within the wall's boundaries
        val relativePositionOnPlane = relativePosition - (normal * (distance))
        val halfWidth = width / 2.0
        val halfHeight = height / 2.0

        return relativePositionOnPlane.x.absoluteValue <= halfWidth &&
                relativePositionOnPlane.y.absoluteValue <= halfHeight &&
                relativePositionOnPlane.z.absoluteValue <= particleRadius
    }
//    asi inicializo el cajon cuadrado:
//    val bottomWall = Wall(Vector(0.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0), L, L)
//    val frontWall = Wall(Vector(0.0, -1.0, 0.0), Vector(0.0, L, 0.0), L, H)
//    val backWall = Wall(Vector(0.0, 1.0, 0.0), Vector(0.0, 0.0, 0.0), L, H)
//    val leftWall = Wall(Vector(1.0, 0.0, 0.0), Vector(0.0, 0.0, 0.0), L, H)
//    val rightWall = Wall(Vector(-1.0, 0.0, 0.0), Vector(L, 0.0, 0.0), L, H)
//
//    val walls = listOf(bottomWall, frontWall, backWall, leftWall, rightWall)

}