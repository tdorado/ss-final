package engine.model

import java.lang.Math.pow
import kotlin.math.pow

data class Particle(
    val id: Int,
    val position: Vector,
    val velocity: Vector,
    val radius: Double,
    val mass: Double,
    val Kt: Double,
    val Kn: Double,
    val gamma: Double,
    var pressure: Double = 0.0,
) {
    fun overlapsWith(otherPosition: Vector, otherRadius: Double): Boolean {
        return position.distance(otherPosition) < (radius + otherRadius)
    }

    fun getKineticEnergy(): Double = 0.5 * mass * velocity.magnitude.pow(2.0)

    fun overlapSize(otherPosition: Vector, otherRadius: Double): Double {
        val relativePosition = otherPosition - position
        val distance = relativePosition.magnitude
        return radius + otherRadius - distance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Particle

        if (id != other.id) return false

        return true
    }

    fun deepCopy(position: Vector, velocity: Vector): Particle {
        return Particle(
            id = id,
            position = Vector(position.x, position.y, position.z),
            velocity = Vector(velocity.x, velocity.y, velocity.z),
            radius = radius,
            mass = mass,
            Kt = Kt,
            Kn = Kn,
            gamma = gamma,
            pressure = pressure,
        )
    }

    fun serialize(): String {
        return "$id,$position,$velocity,$radius,$mass,$Kn,$Kt,$gamma,$pressure"
    }

    companion object {
        fun deserialize(line: String): Particle {
            val parts = line.split(",")
            return Particle(
                parts[0].toInt(),
                Vector.fromString(parts[1]),
                Vector.fromString(parts[2]),
                parts[3].toDouble(),
                parts[4].toDouble(),
                parts[5].toDouble(),
                parts[6].toDouble(),
                parts[7].toDouble(),
                parts[8].toDouble(),
            )
        }
    }

    override fun hashCode(): Int {
        return id
    }
}