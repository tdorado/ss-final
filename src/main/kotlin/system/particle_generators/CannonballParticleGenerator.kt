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
        val radius = particleDiameterGenerator.startInterval
        val rows = (boxSize.y / (2 * radius)).toInt()
        val cols = (boxSize.x / (2 * radius)).toInt()
        val layers = 10

        var count = 1
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                for (layer in 0 until layers) {
                    val x = radius + col * (2.00 * radius)
                    val y = radius + row * (2.00 * radius)
                    val z = radius + layer * (2.00 * radius)

                    val position = Vector(x, y, z)
                    val velocity = Vector()

                    val particle = Particle(
                        count++,
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


                    if (particles.size >= numberOfParticles) {
                        break
                    }
                    particles.add(particle)

                    if (shouldLog) {
                        logger.info("Added particle with id: ${particle.id}, position: $position, velocity: $velocity, radius: $radius")
                    }
                }

                if (particles.size >= numberOfParticles) {
                    break
                }
            }

            if (particles.size >= numberOfParticles) {
                break
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
