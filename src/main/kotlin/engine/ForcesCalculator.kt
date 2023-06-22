package engine

interface ForcesCalculator {
    fun getForces(particle: Particle, position: Vector, velocity: Vector, particles: List<Particle>): Vector
}