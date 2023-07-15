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
    private val lowParticleMass: Double,
    private val boxSize: Vector,
    private val numberOfParticles: Int,
    private val particleDiameterGenerator: ParticleDiameterGenerator,
    private val Kn: Double,
    private val Kt: Double,
    private val gamma: Double,
    private val walls: Set<Wall>,
    private val particleMassGenerator: ParticleMassGenerator = ParticleMassGenerator(
        particleDiameterGenerator.startInterval,
        lowParticleMass
    )
) {
    private val particles = mutableSetOf<Particle>()
    private val logger: Logger = LoggerFactory.getLogger(CannonballParticleGenerator::class.java)
    private var startTime = System.currentTimeMillis()
    private var random = Random

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
                        Kt,
                        Kn,
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
