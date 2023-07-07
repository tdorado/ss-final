package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import engine.model.Wall


class BeemanIntegrator(
    forcesCalculator: ForcesCalculator,
    timeDelta: Double,
    particles: List<Particle>,
    walls: List<Wall>
) : Integrator(forcesCalculator) {
    private val previousAccelerations: MutableMap<Particle, Vector>

    init {
        previousAccelerations = HashMap()
        for (p in particles) {
            val forces = getForces(p, particles, walls)
            val previousPosition = p.position - (p.velocity * (timeDelta)) + (forces * (timeDelta * timeDelta / (2 * p.mass)))
            val previousVelocity = p.velocity - (forces * (timeDelta))
            val previousParticleAux = Particle(p.id, previousPosition, previousVelocity, p.radius, p.mass, p.pressure)
            val previousAcceleration = getForces(previousParticleAux, particles, walls) / (p.mass)
            previousAccelerations[p] = previousAcceleration
        }
    }

    override fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>, walls: List<Wall>) {
        val forces = getForces(particle, particles, walls)
        val acceleration = forces / (particle.mass)
        val previousAcceleration = previousAccelerations[particle]!!
        particle.position = particle.position + (particle.velocity * (timeDelta)) + (acceleration * (2.0 / 3 * timeDelta * timeDelta)) - (previousAcceleration * (1.0 / 6 * timeDelta * timeDelta))
        //predict velocity with position
        val velocityPrediction = particle.velocity + (acceleration * (3.0 / 2 * timeDelta)) - (previousAcceleration * (1.0 / 2 * timeDelta))
        val nextParticlePrediction = Particle(
            particle.id, particle.position, velocityPrediction,
            particle.radius, particle.mass, particle.pressure
        )
        val nextAcceleration = getForces(nextParticlePrediction, particles, walls) / (particle.mass)
        //correct velocity
        particle.velocity = particle.velocity + (nextAcceleration * (1.0 / 3 * timeDelta)) + (acceleration * (5.0 / 6 * timeDelta)) -
                (previousAcceleration * (1.0 / 6 * timeDelta))
        previousAccelerations.replace(particle, acceleration)
    }
}