package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.CannonballParticleGenerator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: List<Particle>,
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

        // check collision with walls
        if (particle.id == 0 && particle.isOnTheGround) {
            if (particle.position.z <= 0 && particle.velocity.z < 0) {
                particle.velocity.z = -particle.velocity.z
            }
        }
        // FIXME
        if (particle.collideWithWall) {
            particle.velocity = particle.velocity * (-1.0)
            acceleration = acceleration * (-1.0)
        }

        val previousAcceleration = previousAccelerations[particle]!!
        particle.position = particle.position +
                (particle.velocity * timeDelta) +
                (acceleration * ((2.0 / 3.0) * timeDelta.pow(2))) -
                (previousAcceleration * (1.0 / 6.0 * timeDelta.pow(2)))
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