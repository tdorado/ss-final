package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import engine.model.Wall
import kotlin.math.pow

class CannonballForcesCalculator(
    private val kn: Double,
    private val kt: Double,
    private val boxWidth: Double,
    private val boxHeight: Double,
    private val boxDepth: Double
) : ForcesCalculator {

    override fun getForces(particle: Particle, neighbours: List<Particle>, walls: List<Wall>): Vector {
        var force = computeParticlesForce(particle, neighbours)
        force = computeWallsForce(particle, walls, force)
        force = applyGravity(particle, force)
        return force
    }

    private fun computeParticlesForce(particle: Particle, neighbours: List<Particle>): Vector {
        var force = Vector(0.0, 0.0, 0.0)
        var pressure = 0.0

        for (neighbour in neighbours) {
            val distance = neighbour.getDistance(particle)
            val direction = particle.position.subtract(neighbour.position).normalize()

            val overlapSize = overlapSize(particle, neighbour)
            if (overlapSize <= 0) continue  // Not colliding

            val relativeVelocity = particle.velocity.subtract(neighbour.velocity).dotProduct(direction)
            val normalForceValue = -kn * overlapSize
            val tangentialForceValue = -kt * overlapSize * relativeVelocity

            force = force.add(
                direction.multiply(normalForceValue).add(
                    direction.crossProduct(Vector(0.0, 0.0, 1.0)).multiply(tangentialForceValue)
                )
            )
            pressure += normalForceValue
        }

        particle.pressure = (Math.abs(pressure) / (4 * Math.PI * particle.radius.pow(2)))
        return force
    }

    private fun computeWallsForce(particle: Particle, walls: List<Wall>, force: Vector): Vector {
        var force = force
        for (wall in walls) {
            val overlapSize = overlapSize(particle, wall)
            if (overlapSize <= 0) continue  // Not touching the wall

            val relativeVelocity = particle.velocity.dotProduct(wall.getNormal())
            val forceNormalAndTan = wall.getNormal().multiply(-kn * overlapSize).add(
                wall.getTangent().multiply(-kt * overlapSize * relativeVelocity)
            )

            force = force.add(forceNormalAndTan)
        }
        return force
    }

    private fun applyGravity(particle: Particle, force: Vector): Vector {
        return force.add(Vector(0.0, -CannonballConstants.getGravity() * particle.mass, 0.0))
    }

    private fun overlapSize(one: Particle, another: Particle): Double {
        val overlapSize = one.radius + another.radius - one.getDistance(another)
        return maxOf(overlapSize, 0.0)
    }

    private fun overlapSize(p: Particle, wall: Wall): Double {
        return maxOf(p.radius - p.position.subtract(wall.getPoint()).dotProduct(wall.getNormal()), 0.0)
    }
}