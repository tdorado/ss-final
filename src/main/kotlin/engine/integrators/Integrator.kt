package engine.integrators

import engine.ForcesCalculator
import engine.Particle
import engine.Vector

abstract class Integrator(private val forcesCalculator: ForcesCalculator) {
    fun getForces(particle: Particle, position: Vector, velocity: Vector, particles: List<Particle>): Vector {
        return forcesCalculator.getForces(particle, position, velocity, particles)
    }

    abstract fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>)
}