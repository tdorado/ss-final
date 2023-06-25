package engine.model

import kotlin.math.pow
import kotlin.math.sqrt

data class Vector(val x: Double, val y: Double, val z: Double) {
    fun dotProduct(other: Vector): Double {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }

    fun subtract(other: Vector): Vector {
        return Vector(this.x - other.x, this.y - other.y, this.z - other.z)
    }

    fun add(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y, this.z + other.z)
    }

    fun multiply(scalar: Double): Vector {
        return Vector(this.x * scalar, this.y * scalar, this.z * scalar)
    }

    fun length(): Double {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
    }
    fun normalize(): Vector {
        val magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
        return Vector(this.x / magnitude, this.y / magnitude, this.z / magnitude)
    }

    fun divide(number: Double): Vector {
        return Vector(x / number, y / number, z / number)
    }

    fun distance(vector: Vector): Double {
        return sqrt((x - vector.x).pow(2.0) + (y - vector.y).pow(2.0) + (z - vector.z).pow(2.0))
    }
    fun crossProduct(other: Vector): Vector {
        val newX = this.y * other.z - this.z * other.y
        val newY = this.z * other.x - this.x * other.z
        val newZ = this.x * other.y - this.y * other.x
        return Vector(newX, newY, newZ)
    }


}