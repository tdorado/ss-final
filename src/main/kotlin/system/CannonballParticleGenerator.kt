package system

import engine.model.Particle
import engine.model.Vector
import engine.model.Wall
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

class CannonballParticleGenerator(
    private val minRadius: Double,
    private val maxRadius: Double,
    private val boxSize: Vector,
    private val numberOfParticles: Int,
    private val maxVelocity: Double,
    private val walls: List<Wall>,
) {
    private val particles = mutableListOf<Particle>()
    private val random = Random
    private val logger: Logger = LoggerFactory.getLogger(CannonballParticleGenerator::class.java)
    private var progress = 0
    private var startTime = System.currentTimeMillis()

    fun generateParticles(shouldLog: Boolean = false): List<Particle> {
        startTime = System.currentTimeMillis()

        var particleCount = 1 // empieza en 1 asi la bala de cañon es la 0
        while (particles.size < numberOfParticles) {
            val radius = random.nextDouble(minRadius, maxRadius)
            val position = Vector(
                random.nextDouble(radius, boxSize.x - radius),
                random.nextDouble(radius, boxSize.y - radius),
                random.nextDouble(radius, boxSize.z - radius)
            )

            if (particles.none { it.overlapsWith(position, radius) } && walls.none {
                    it.overlapsWith(
                        position,
                        radius
                    )
                }) {
                val velocity = Vector(
                    random.nextDouble(-maxVelocity, maxVelocity),
                    random.nextDouble(-maxVelocity, maxVelocity),
                    random.nextDouble(-maxVelocity, maxVelocity)
                )
                particles.add(Particle(particleCount++, position, velocity, radius, 1.0, 0.0))

                if (shouldLog) {
                    logger.info("Added particle with position: $position, velocity: $velocity, radius: $radius")
                }
                updateProgress()
            } else {
                if (shouldLog)
                    logger.info("Failed to add particle with position: $position, radius: $radius, overlapped with existing particle or wall")
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
