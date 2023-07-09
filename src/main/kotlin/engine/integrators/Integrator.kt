package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector

abstract class Integrator(private val forcesCalculator: ForcesCalculator) {
    fun getForces(particle: Particle, particles: Set<Particle>): Vector {
        return forcesCalculator.getForces(particle, particles)
    }

    abstract fun applyIntegrator(timeDelta: Double, particle: Particle, particles: Set<Particle>)
}