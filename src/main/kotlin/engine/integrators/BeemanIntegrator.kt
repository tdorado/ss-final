package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: List<Particle>
) : Integrator(forcesCalculator) {
    private val previousAccelerations: MutableMap<Particle, Vector>

    init {
        previousAccelerations = HashMap()
        for (p in particles) {
            val forces: Vector = getForces(p, p.position, p.velocity, particles)
            val previousPosition: Vector = p.position.subtract(p.velocity.multiply(timeDelta)).add(
                forces.multiply(
                    timeDelta * timeDelta / (2 * p.mass)
                )
            )
            val previousVelocity: Vector = p.velocity.subtract(forces.multiply(timeDelta))
            val previousAcceleration: Vector =
                getForces(p, previousPosition, previousVelocity, particles).divide(p.mass)
            previousAccelerations[p] = previousAcceleration
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>) {
        val forces: Vector = getForces(particle, particle.position, particle.velocity, particles)
        val acceleration: Vector = forces.divide(particle.mass)
        val previousAcceleration: Vector = previousAccelerations[particle]!!
        particle.position = particle.position.add(particle.velocity.multiply(timeDelta)).add(
                acceleration.multiply(
                    2.0 / 3 * timeDelta * timeDelta
                )
            ).subtract(previousAcceleration.multiply(1.0 / 6 * timeDelta * timeDelta))
        //predict velocity with position
        val velocityPrediction: Vector =
            particle.velocity.add(acceleration.multiply(3.0 / 2 * timeDelta)).subtract(
                previousAcceleration.multiply(
                    1.0 / 2 * timeDelta
                )
            )
        val nextAcceleration: Vector =
            getForces(particle, particle.position, velocityPrediction, particles).divide(particle.mass)
        //correct velocity
        particle.velocity = particle.velocity.add(nextAcceleration.multiply(1.0 / 3 * timeDelta)).add(
                acceleration.multiply(
                    5.0 / 6 * timeDelta
                )
            ).subtract(previousAcceleration.multiply(1.0 / 6 * timeDelta))
        previousAccelerations.replace(particle, acceleration)
    }
}