package engine.model

import kotlin.math.sqrt

class Vector(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar, z * scalar)

    operator fun div(scalar: Double) = Vector(x / scalar, y / scalar, z / scalar)
    infix fun dot(v: Vector) = x * v.x + y * v.y + z * v.z

    fun crossProduct(other: Vector): Vector {
        val newX = y * other.z - z * other.y
        val newY = z * other.x - x * other.z
        val newZ = x * other.y - y * other.x
        return Vector(newX, newY, newZ)
    }
    fun normalize(): Vector {
        val magnitude = sqrt(dot(this))
        return Vector(x / magnitude, y / magnitude, z / magnitude)
    }

    fun distance(other: Vector): Double {
        return sqrt(x * other.x + y * other.y + z * other.z)
    }
}