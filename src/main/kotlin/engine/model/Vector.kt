package engine.model

import kotlin.math.*

data class Vector(val x: Double, val y: Double, val z: Double) {
    fun dotProduct(other: Vector): Double {
        return x * other.x + y * other.y + z * other.z
    }

    fun subtract(other: Vector): Vector {
        return Vector(x - other.x, y - other.y, z - other.z)
    }

    fun add(other: Vector): Vector {
        return Vector(x + other.x, y + other.y, z + other.z)
    }

    fun multiply(scalar: Double): Vector {
        return Vector(x * scalar, y * scalar, z * scalar)
    }

    fun length(): Double {
        return sqrt(x * x + y * y + z * z)
    }

    fun normalize(): Vector {
        val magnitude = sqrt(x * x + y * y + z * z)
        return Vector(x / magnitude, y / magnitude, z / magnitude)
    }

    fun divide(number: Double): Vector {
        return Vector(x / number, y / number, z / number)
    }

    fun distance(vector: Vector): Double {
        return sqrt((x - vector.x).pow(2.0) + (y - vector.y).pow(2.0) + (z - vector.z).pow(2.0))
    }

    fun crossProduct(other: Vector): Vector {
        val newX = y * other.z - z * other.y
        val newY = z * other.x - x * other.z
        val newZ = x * other.y - y * other.x
        return Vector(newX, newY, newZ)
    }

    fun rotate(angle: Double): Vector {
        val newX = x * cos(angle) + z * sin(angle)
        val newZ = -x * sin(angle) + z * cos(angle)
        return Vector(newX, y, newZ)
    }

    fun angle(other: Vector): Double {
        val dotProduct = dotProduct(other)
        val magnitudes = length() * other.length()
        return acos(dotProduct / magnitudes)
    }
}