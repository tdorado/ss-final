package engine.model

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import kotlin.math.pow

data class Particle(
    val id: Int,
    val position: Vector,
    val velocity: Vector,
    val radius: Double,
    val mass: Double,
    val kt: Double,
    val kn: Double,
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

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)

        fun saveParticlesToFile(particles: Set<Particle>, fileName: String) {
            val file = File("$fileName.dat")
            if (file.exists()) {
                file.renameTo(File("$fileName.old.dat"))
                logger.info("Existing file renamed to: $fileName.old.dat")
            }
            ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(particles) }
        }

        fun loadParticlesFromFile(fileName: String): Set<Particle> {
            return ObjectInputStream(FileInputStream("$fileName.dat")).use { it.readObject() as Set<Particle> }
        }
    }

    override fun hashCode(): Int {
        return id
    }
}