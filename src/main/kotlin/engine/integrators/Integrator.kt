package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector

abstract class Integrator(private val forcesCalculator: ForcesCalculator) {
    fun getForces(particle: Particle, position: Vector, velocity: Vector, particles: List<Particle>): Vector {
//        return forcesCalculator.getForces(particle, position, velocity, particles)
        return Vector(1.0, 1.0, 1.0)
    }

    abstract fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>)
}