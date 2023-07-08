package engine.model

import kotlin.math.sqrt

class Vector(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    private val magnitude: Double
        get() = sqrt(x * x + y * y + z * z)
    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar, z * scalar)

    operator fun div(scalar: Double) = Vector(x / scalar, y / scalar, z / scalar)
    operator fun unaryMinus() = Vector(-x, -y, -z)

    fun dotProduct(v: Vector) = x * v.x + y * v.y + z * v.z

    fun crossProduct(v: Vector) = Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

    fun normalize(): Vector {
        val magnitude = magnitude
        return Vector(x / magnitude, y / magnitude, z / magnitude)
    }

    fun distance(v: Vector) = (this - v).magnitude
}