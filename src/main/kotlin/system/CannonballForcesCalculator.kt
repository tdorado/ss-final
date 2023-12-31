package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import mu.KotlinLogging

class CannonballForcesCalculator(private val gravity: Double, private val walls: Set<Wall>) : ForcesCalculator {
    private val logger = KotlinLogging.logger {}

    private fun calculateGravityForce(particle: Particle): Vector {
        return Vector(0.0, 0.0, -particle.mass * gravity)
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val overlapSize = particle.overlapSize(otherParticle.position, otherParticle.radius)
                if (overlapSize > 0.0) {
                    val relativePosition = otherParticle.position - particle.position
                    val normalVector = relativePosition.normalize()

                    val relativeVelocity = particle.velocity - otherParticle.velocity
                    val normalRelativeVelocity = relativeVelocity.dotProduct(normalVector)
                    val tangentialRelativeVelocity = relativeVelocity - normalVector * normalRelativeVelocity

                    val normalForceMagnitude = -particle.kn * overlapSize - particle.gamma * normalRelativeVelocity
                    val tangentialForceMagnitude = -particle.kt * overlapSize

                    val normalForceValue = normalVector * normalForceMagnitude
                    val tangentialForceValue = tangentialRelativeVelocity.normalize() * tangentialForceMagnitude

                    interactionForce += normalForceValue + tangentialForceValue
                }
            }
        }
        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            val overlapSize = calculateOverlapSizeWithWall(particle, wall)
            if (overlapSize > 0.0) {
                val normalRelativeVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentialRelativeVelocity = particle.velocity - wall.normal * normalRelativeVelocity

                val normalForceMagnitude = -wall.kn * overlapSize - wall.gamma * normalRelativeVelocity
                val tangentialForceMagnitude = -wall.kt * overlapSize

                val normalForceValue = -wall.normal * normalForceMagnitude
                val tangentialForceValue = tangentialRelativeVelocity.normalize() * tangentialForceMagnitude

                wallForce += normalForceValue + tangentialForceValue
            }
        }

        return wallForce
    }

    private fun calculateOverlapSizeWithWall(particle: Particle, wall: Wall): Double {
        val distanceToWall = (particle.position - wall.position).dotProduct(wall.normal)
        return particle.radius - distanceToWall
    }


    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
        val gravityForce = calculateGravityForce(particle)
        val totalForces = interactionForce + wallForce + gravityForce
        particle.setPressure(totalForces)
        return totalForces
    }
}
