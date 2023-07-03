package engine

import engine.model.Particle
import engine.model.Vector
import engine.model.Wall

interface ForcesCalculator {
//    fun getForces(particle: Particle, position: Vector, velocity: Vector, particles: List<Particle>): Vector
    fun getForces(particle: Particle, neighbours: List<Particle>, walls: List<Wall>) : Vector
}