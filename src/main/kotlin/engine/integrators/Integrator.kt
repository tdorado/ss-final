package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector

abstract class Integrator(private val forcesCalculator: ForcesCalculator) {
    fun getForces(particle: Particle, particles: List<Particle>): Vector {
        return forcesCalculator.getForces(particle, particles)
    }

    abstract fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>)
}