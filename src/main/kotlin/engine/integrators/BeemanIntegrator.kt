package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.Wall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: Set<Particle>,
    val walls: Set<Wall>
) : Integrator(forcesCalculator) {
    private val previousAccelerations: MutableMap<Int, Vector>
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        previousAccelerations = HashMap()
        val zeroV = Vector()


        for (p in particles) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] Performing simulation for particle " + p.id)
            if (p.velocity == zeroV) {
                previousAccelerations[p.id] = zeroV
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
                        p.Kt,
                        p.Kn,
                        p.gammaT,
                        p.gammaN,
                        p.pressure
                    )

                if (previousParticleAux.Kt.isNaN()){
                    System.out.println("NaN")
                }
                val previousAcceleration = getForces(previousParticleAux, particles) / (p.mass)
                previousAccelerations[p.id] = previousAcceleration
            }
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: Set<Particle>) : Particle {
        val acceleration = getForces(particle, particles) / particle.mass

        val previousAcceleration = previousAccelerations[particle.id]!!
        val position = particle.position +
                (particle.velocity * timeDelta) +
                (acceleration * ((2.0 / 3.0) * timeDelta.pow(2))) -
                (previousAcceleration * (1.0 / 6.0 * timeDelta.pow(2)))

        if (position.x.isNaN() || position.y.isNaN() || position.z.isNaN()){
            System.out.println("")
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
            particle.Kt,
            particle.Kn,
            particle.gammaT,
            particle.gammaN,
            particle.pressure
        )
        if (particle.Kt.isNaN() || nextParticlePrediction.Kt.isNaN()){
            System.out.println("")
        }
        val nextAcceleration = getForces(nextParticlePrediction, particles) / particle.mass

        //correct velocity
        val velocity = particle.velocity +
                (nextAcceleration * (1.0 / 3.0 * timeDelta)) +
                (acceleration * (5.0 / 6.0 * timeDelta)) -
                (previousAcceleration * (1.0 / 6.0 * timeDelta))
        if (particle.id == 1) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] new position for cannonball: ${particle.position} acceleration $acceleration velocity $velocity")
        }

        previousAccelerations[particle.id] = acceleration
        return particle.copy(position = position, velocity = velocity)
    }
}