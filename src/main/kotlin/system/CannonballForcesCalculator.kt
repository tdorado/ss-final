package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import kotlin.math.pow
class CannonballForcesCalculator(private val walls: List<Wall>, private val boxHeight: Double = 1.0) : ForcesCalculator {

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    private fun calculatePressure(particle: Particle, boxHeight: Double): Double {
        val weight = particle.mass * 9.81 // Weight = mass * g
        val heightRatio = (boxHeight - particle.position.z) / boxHeight
        return weight * heightRatio / (2 * Math.PI * particle.radius.pow(2.0))
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: List<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle && particle.overlapsWith(otherParticle.position, otherParticle.radius)) {
                val normalDirection = (otherParticle.position - particle.position).normalize()
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val normalVelocity = normalDirection * (relativeVelocity.dotProduct(normalDirection))
                val tangentialVelocity = relativeVelocity - normalVelocity

                interactionForce += tangentialVelocity * -particle.frictionCoefficient
            }
        }

        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: List<Wall>): Vector {
        var wallForce = Vector()
        particle.collideWithWall = false

        for (wall in walls) {
            if (wall.overlapsWith(particle.position, particle.radius)) {
                val normalVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentVelocity = particle.velocity.crossProduct(wall.tangent).magnitude

                wallForce += wall.normal * -2.0 * normalVelocity * particle.frictionCoefficient
                wallForce += wall.tangent * -2.0 * tangentVelocity * particle.frictionCoefficient
                particle.collideWithWall = true
            }
        }

        return wallForce
    }

    override fun getForces(particle: Particle, neighbours: List<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)

        // Prevent the particle from passing through the floor (z = 0) FIXME extract to another place
        if (particle.position.z < 0.0 && particle.id == 0) {
            particle.isOnTheGround = true
            val normalForce = Vector(0.0, 0.0, -gravityForce.z)
            return interactionForce + wallForce + normalForce
        } else if (particle.id == 0) {
            particle.isOnTheGround = false
        }

        return gravityForce + interactionForce + wallForce
    }
}