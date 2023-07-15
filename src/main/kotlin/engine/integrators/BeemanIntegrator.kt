package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import kotlin.math.pow


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: Set<Particle>
) : Integrator(forcesCalculator) {

    init {
        for (particle in particles) {
            if (particle.velocity != Vector()) {
                val forces = getForces(particle, particles)
                val previousPosition = particle.position -
                        particle.velocity * timeDelta +
                        forces * (timeDelta * timeDelta / (2 * particle.mass))
                val previousVelocity = particle.velocity - forces * timeDelta
                val previousParticleStep = particle.copy(position = previousPosition, velocity = previousVelocity)
                val previousAcceleration = getForces(previousParticleStep, particles) / particle.mass
                particle.previousAcceleration = previousAcceleration
            }
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: Set<Particle>): Particle {
        // Calculate current acceleration
        val currentAcceleration = getForces(particle, particles) / particle.mass

        // Fetch the previous acceleration
        val previousAcceleration = particle.previousAcceleration

        // Position update
        val newPosition = particle.position +
                particle.velocity * timeDelta +
                currentAcceleration * (2.0 / 3.0) * timeDelta.pow(2) -
                previousAcceleration * (1.0 / 3.0) * timeDelta.pow(2)

        // Predicted velocity
        val predictedVelocity = particle.velocity +
                currentAcceleration * (3.0 / 2.0) * timeDelta -
                previousAcceleration * (1.0 / 2.0) * timeDelta

        // Calculate new acceleration using predicted velocity
        val newParticle = particle.copy(velocity = predictedVelocity, position = newPosition)
        val newAcceleration = getForces(newParticle, particles) / particle.mass

        // Correct the velocity with the predicted acceleration
        val newVelocity = particle.velocity +
                newAcceleration * (1.0 / 3.0) * timeDelta +
                currentAcceleration * (5.0 / 6.0) * timeDelta -
                previousAcceleration * (1.0 / 6.0) * timeDelta

        // Store the current acceleration as the "previousAcceleration" for the next timestep
        particle.previousAcceleration = currentAcceleration

        return particle.copy(position = newPosition, velocity = newVelocity)
    }

}