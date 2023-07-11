package engine.model

import kotlin.math.sqrt

class Vector(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {

    companion object {
        fun fromString(str: String): Vector {
            val parts = str.trimStart('(').trimEnd(')').split(';')
            return Vector(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
        }
    }

    val magnitude: Double
        get() = sqrt(x * x + y * y + z * z)

    fun isParallelTo(other: Vector): Boolean {
        // Calcular el producto cruz entre los dos vectores
        val crossProduct = this.crossProduct(other)

        // Los vectores son paralelos si la magnitud del producto cruz es cerca de cero.
        // Usamos una pequeña tolerancia en lugar de verificar si es exactamente cero
        // debido a la posibilidad de errores de precisión numérica.
        return crossProduct.magnitude < 1e-6
    }

    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar, z * scalar)

    operator fun div(scalar: Double) = Vector(x / scalar, y / scalar, z / scalar)
    operator fun unaryMinus() = Vector(-x, -y, -z)
    fun projectOnPlane(planeNormal: Vector): Vector {
        val normalizedPlaneNormal = planeNormal.normalize()
        if (normalizedPlaneNormal == Vector()) {
            return Vector()
        }
        return this - (normalizedPlaneNormal.times(this.dotProduct(normalizedPlaneNormal)))
    }
    fun squaredMagnitude(): Double {
        return this.dotProduct(this)
    }
    fun dotProduct(v: Vector) = x * v.x + y * v.y + z * v.z

    fun crossProduct(v: Vector) = Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

    fun normalize(): Vector {
        val magnitude = magnitude
        if (magnitude == 0.0) return Vector()
        return Vector(x / magnitude, y / magnitude, z / magnitude)
    }

    fun distance(v: Vector) = (this - v).magnitude

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "($x; $y; $z)"
    }
}