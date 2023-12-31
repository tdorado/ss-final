package engine

import engine.model.Particle
import engine.model.Vector

interface ForcesCalculator {
    fun getForces(particle: Particle, neighbours: Set<Particle>): Vector
}