package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.CannonballParticleGenerator
import system.Wall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: List<Particle>,
    val walls: List<Wall>
) : Integrator(forcesCalculator) {
    private val previousAccelerations: MutableMap<Particle, Vector>
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        previousAccelerations = HashMap()
        val zeroV = Vector()
        for (p in particles) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] Performing simulation for particle " + p.id)
            if (p.velocity == zeroV) {
                previousAccelerations[p] = zeroV
            } else {
                val forces = getForces(p, particles)
                val previousPosition =
                    p.position - (p.velocity * (timeDelta)) + (forces * (timeDelta * timeDelta / (2 * p.mass)))
                val previousVelocity = p.velocity - (forces * (timeDelta))
                val previousParticleAux =
                    Particle(
                        p.id,
                        previousPosition,
                        previousVelocity,
                        p.radius,
                        p.mass,
                        p.frictionCoefficient,
                        p.pressure
                    )
                val previousAcceleration = getForces(previousParticleAux, particles) / (p.mass)
                previousAccelerations[p] = previousAcceleration
            }
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>) {
        var acceleration = getForces(particle, particles) / particle.mass

        val previousAcceleration = previousAccelerations[particle]!!
        particle.position = particle.position +
                (particle.velocity * timeDelta) +
                (acceleration * ((2.0 / 3.0) * timeDelta.pow(2))) -
                (previousAcceleration * (1.0 / 6.0 * timeDelta.pow(2)))

        // Check each wall for collision
        for (wall in walls) {
            if (wall.overlapsWith(particle.position, particle.radius)) {
                // The particle has collided with this wall, so we reflect its position and velocity along the normal of the wall
                val relativePosition = particle.position - wall.point
                val distanceFromWall = relativePosition.dotProduct(wall.normal)
                if (distanceFromWall < 0) {
                    particle.position = particle.position - (wall.normal.times(2 * distanceFromWall))
                    particle.velocity = particle.velocity - (wall.normal.times(2 * particle.velocity.dotProduct(wall.normal)))
                }
            }
        }
        //predict velocity with position
        val velocityPrediction = particle.velocity +
                (acceleration * (3.0 / 2.0 * timeDelta)) -
                (previousAcceleration * (1.0 / 2.0 * timeDelta))
        val nextParticlePrediction = Particle(
            particle.id,
            particle.position,
            velocityPrediction,
            particle.radius,
            particle.mass,
            particle.frictionCoefficient,
            particle.pressure
        )
        val nextAcceleration = getForces(nextParticlePrediction, particles) / particle.mass

        //correct velocity
        particle.velocity = particle.velocity +
                (nextAcceleration * (1.0 / 3.0 * timeDelta)) +
                (acceleration * (5.0 / 6.0 * timeDelta)) -
                (previousAcceleration * (1.0 / 6.0 * timeDelta))




        previousAccelerations[particle] = acceleration
    }
}