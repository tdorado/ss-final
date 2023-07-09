package system.particle_generators

import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.Wall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class CannonballParticleGenerator(
    private val mass: Double,
    private val boxSize: Vector,
    private val numberOfParticles: Int,
    private val walls: Set<Wall>,
    private val particleDiameterGenerator: ParticleDiameterGenerator,
    private val Kn: Double,
    private val Kt: Double,
    private val gammaN: Double,
    private val gammaT: Double,
    private val pressure: Double = 0.0
) {
    private val particles = mutableSetOf<Particle>()
    private val random = Random
    private val logger: Logger = LoggerFactory.getLogger(CannonballParticleGenerator::class.java)
    private var progress = 0
    private var startTime = System.currentTimeMillis()

    fun generateParticles(shouldLog: Boolean = false): Set<Particle> {
        startTime = System.currentTimeMillis()

        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        logger.info("[$formattedDateTime] Generating particles")
        var particleCount = 1 // Empieza en 1 para que la bala de cañón sea la 0

        var zPosition = 0.0
        var overlapCount = 0
        while (particles.size < numberOfParticles) {
            val radius = particleDiameterGenerator.getParticleDiameter() / 2 // Radio = diámetro / 2

            val position = Vector(
                random.nextDouble(radius, boxSize.x - radius),
                random.nextDouble(radius, boxSize.y - radius),
                zPosition + radius
            )

            val overlappingParticle = particles.find { it.overlapsWith(position, radius) }
            val overlappingWall = walls.find { it.overlapsWithParticle(position, radius) }

            if (overlappingParticle == null && overlappingWall == null) {
                val velocity = Vector()
                particles.add(
                    Particle(
                        particleCount++,
                        position,
                        velocity,
                        radius,
                        mass,
                        Kt,
                        Kn,
                        gammaT,
                        gammaN,
                        pressure
                    )
                )

                if (shouldLog) {
                    logger.info("Added particle with position: $position, velocity: $velocity, radius: $radius")
                }
                updateProgress()
                overlapCount = 0
            } else {
                overlapCount++
                if (overlapCount > 100) {
                    zPosition += radius
                    overlapCount = 0
                }

                if (shouldLog) {
                    if (overlappingParticle != null) {
                        logger.info("Failed to add particle with position: $position, radius: $radius, overlapped with existing particle: $overlappingParticle")
                    } else {
                        logger.info("Failed to add particle with position: $position, radius: $radius, overlapped with wall: $overlappingWall")
                    }
                }
            }
        }

        return particles
    }


    private fun updateProgress() {
        progress += 1
        val progressBar = StringBuilder("\rProgress: [")
        for (i in 0 until progress * 100 / numberOfParticles) {
            progressBar.append("#")
        }
        for (i in progress * 100 / numberOfParticles until 100) {
            progressBar.append(" ")
        }
        progressBar.append("]")

        val percent = (progress * 100.0) / numberOfParticles
        val elapsed = System.currentTimeMillis() - startTime
        val estimatedTotal = elapsed / percent * 100
        val remaining = estimatedTotal - elapsed

        progressBar.append(
            " ${
                String.format(
                    "%.2f",
                    percent
                )
            }% complete, estimated remaining time: ${remaining / 1000}s"
        )

        logger.info(progressBar.toString())
    }
}
