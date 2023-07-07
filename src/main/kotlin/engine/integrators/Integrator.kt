package engine.integrators

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import engine.model.Wall

abstract class Integrator(private val forcesCalculator: ForcesCalculator) {
    fun getForces(particle: Particle, particles: List<Particle>, walls: List<Wall>): Vector {
        return forcesCalculator.getForces(particle, particles, walls)
    }

    abstract fun applyIntegrator(timeDelta: Double, particle: Particle, particles: List<Particle>, walls: List<Wall>)
}