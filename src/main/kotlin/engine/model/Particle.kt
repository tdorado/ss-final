package engine.model

import java.io.*
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
    var previousAcceleration: Vector = Vector()
) : Serializable {
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

        return id == other.id
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
            previousAcceleration = previousAcceleration,
        )
    }


    companion object {
        fun saveParticlesToFile(particles: Set<Particle>, fileName: String) {
            ObjectOutputStream(FileOutputStream(fileName)).use { it.writeObject(particles) }
        }

        fun loadParticlesFromFile(fileName: String): Set<Particle> {
            return ObjectInputStream(FileInputStream(fileName)).use { it.readObject() as Set<Particle> }
        }
    }

    override fun hashCode(): Int {
        return id
    }
}