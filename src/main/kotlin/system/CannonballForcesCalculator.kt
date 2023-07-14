package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CannonballForcesCalculator(private val walls: Set<Wall>) : ForcesCalculator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val overlapLimit = 0.0
    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val overlapSize = particle.overlapSize(otherParticle.position, otherParticle.radius)
                if (overlapSize > overlapLimit) {
                    val relativePosition = otherParticle.position - particle.position
                    val normalVector = relativePosition.normalize()

                    val relativeVelocity = particle.velocity - otherParticle.velocity
                    val relativeNormalVelocity = relativeVelocity.dotProduct(normalVector)
                    val relativeTangentVelocity = relativeVelocity - normalVector * relativeNormalVelocity

                    val normalForceMagnitude = particle.Kn * overlapSize + particle.gammaN * relativeNormalVelocity
                    val tangentialForceMagnitude = particle.Kt * overlapSize

                    val normalForceValue = -normalVector * normalForceMagnitude
                    val tangentialForceValue = -relativeTangentVelocity.normalize() * tangentialForceMagnitude

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
            if (overlapSize > overlapLimit) {
                val relativeVelocity = particle.velocity - wall.normal * particle.velocity.dotProduct(wall.normal)

                val normalForceMagnitude =
                    -3E3 * overlapSize - particle.gammaN * relativeVelocity.dotProduct(wall.normal)
                val tangentialForceMagnitude = 6E3 * overlapSize

                val normalForceValue = -wall.normal * normalForceMagnitude
                val tangentialForceValue = -relativeVelocity.normalize() * tangentialForceMagnitude

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
        return gravityForce + interactionForce + wallForce
    }
}
