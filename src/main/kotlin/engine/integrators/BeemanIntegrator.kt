package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.Wall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.pow


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: Set<Particle>
) : Integrator(forcesCalculator) {
    private val previousAccelerations: MutableMap<Int, Vector>

    init {
        previousAccelerations = HashMap()
        val zeroV = Vector()
        for (p in particles) {
            if (p.velocity == zeroV) {
                previousAccelerations[p.id] = zeroV
            } else {
                val forces = getForces(p, particles)
                val previousPosition =
                    p.position - (p.velocity * (timeDelta)) + (forces * (timeDelta * timeDelta / (2 * p.mass)))
                val previousVelocity = p.velocity - (forces * (timeDelta))
                val previousParticleAux = p.copy(position = previousPosition, velocity = previousVelocity)
                val previousAcceleration = getForces(previousParticleAux, particles) / (p.mass)
                previousAccelerations[p.id] = previousAcceleration
            }
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: Set<Particle>): Particle {
        // Calculate current acceleration
        val currentAcceleration = getForces(particle, particles) / particle.mass

        // Fetch the previous acceleration
        val previousAcceleration = previousAccelerations[particle.id]!!

        // Position update
        val newPosition = particle.position +
                particle.velocity * timeDelta +
                currentAcceleration.times((2.0 / 3.0)) * timeDelta.pow(2) -
                previousAcceleration.times((1.0 / 6.0)) * timeDelta.pow(2)

        // Predict the velocity for the next timestep
        val predictedVelocity = particle.velocity +
                currentAcceleration.times((3.0 / 2.0)) * timeDelta -
                previousAcceleration.times((1.0 / 2.0)) * timeDelta

        // Get the predicted acceleration
        val predictedParticle = particle.deepCopy(position = newPosition, velocity = predictedVelocity)

        // The "next" acceleration should be calculated with the updated position and velocity
        val nextAcceleration = getForces(predictedParticle, particles) / predictedParticle.mass

        // Correct the velocity with the predicted acceleration
        val newVelocity = particle.velocity +
                (nextAcceleration.times((1.0 / 3.0)) * timeDelta) +
                (currentAcceleration.times((5.0 / 6.0)) * timeDelta) -
                (previousAcceleration.times((1.0 / 6.0)) * timeDelta)

        // Store the current acceleration as the "previousAcceleration" for the next timestep
        previousAccelerations[particle.id] = currentAcceleration

        return particle.copy(position = newPosition, velocity = newVelocity)
    }

}