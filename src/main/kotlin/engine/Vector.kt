package engine

import kotlin.math.pow
import kotlin.math.sqrt

class Vector(
    var x: Double,
    var y: Double,
    var z: Double
) {
    fun add(vector: Vector): Vector {
        return Vector(x + vector.x, y + vector.y, z + vector.z)
    }

    fun subtract(vector: Vector): Vector {
        return Vector(x - vector.x, y - vector.y, z - vector.z)
    }

    fun multiply(number: Double): Vector {
        return Vector(x * number, y * number, z * number)
    }

    fun multiply(vector: Vector): Vector {
        return Vector(x * vector.x, y * vector.y, z * vector.z)
    }

    fun divide(number: Double): Vector {
        return Vector(x / number, y / number, z / number)
    }

    fun distance(vector: Vector): Double {
        return sqrt((x - vector.x).pow(2.0) + (y - vector.y).pow(2.0) + (z - vector.z).pow(2.0))
    }

    fun changeSign(): Vector {
        return Vector(-x, -y, -z)
    }

    fun copy(vector: Vector): Vector {
        return Vector(vector.x, vector.y, vector.z)
    }
}