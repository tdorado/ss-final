package system.generator

import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.Wall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class CannonballParticleGenerator(
    private val lowParticleMass: Double,
    private val boxSize: Vector,
    private val numberOfParticles: Int,
    private val particleDiameterGenerator: ParticleDiameterGenerator,
    private val kn: Double,
    private val kt: Double,
    private val gamma: Double,
    private val walls: Set<Wall>,
    private val particleMassGenerator: ParticleMassGenerator = ParticleMassGenerator(
        particleDiameterGenerator.startInterval,
        lowParticleMass
    )
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val particles = mutableSetOf<Particle>()
    private var random = Random

    fun generateParticles(shouldLog: Boolean = false): Set<Particle> {
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        logger.info("[$formattedDateTime] Generating particles")
        var particleCount = 1

        var zPosition = 0.0
        var overlapCount = 0
        while (particles.size < numberOfParticles) {
            val radius = particleDiameterGenerator.getParticleDiameter() / 2

            val position = Vector(
                random.nextDouble(radius, boxSize.x - radius),
                random.nextDouble(radius, boxSize.y - radius),
                zPosition + radius
            )

            val overlappingParticle = particles.find { it.overlapsWith(position, radius) }
            val overlappingWall = walls.find { it.overlapsWith(position, radius) }

            if (overlappingParticle == null && overlappingWall == null) {
                val velocity = Vector()
                particles.add(
                    Particle(
                        particleCount++,
                        position,
                        velocity,
                        radius,
                        particleMassGenerator.getParticleMass(radius * 2),
                        kt,
                        kn,
                        gamma,
                    )
                )

                if (shouldLog) {
                    logger.info("Added particle with position: $position, velocity: $velocity, radius: $radius")
                }
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

        return particles.toSet()
    }
}
