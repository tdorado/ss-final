package engine

import engine.model.Particle
import engine.model.Vector
import engine.model.Wall

interface ForcesCalculator {
    fun getForces(particle: Particle, neighbours: List<Particle>, walls: List<Wall>): Vector
}