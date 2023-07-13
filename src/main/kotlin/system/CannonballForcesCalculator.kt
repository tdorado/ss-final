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
        if (particle.position.z < particle.radius) {
            return Vector()
        }
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

                    // Invierto el signo del término de amortiguación
                    val normalForceMagnitude = particle.Kn * overlapSize + particle.gammaN * relativeNormalVelocity
                    val tangentialForceMagnitude =
                        particle.Kt * overlapSize + particle.gammaT * relativeTangentVelocity.magnitude

                    val normalForceValue = -normalVector * normalForceMagnitude
                    val tangentialForceValue = -relativeTangentVelocity.normalize() * tangentialForceMagnitude

                    interactionForce += normalForceValue
                }
            }
        }


        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            val overlapSize = calculateOverlapSizeWithWall(particle, wall)

            if (particle.id == 99 && particle.position.z < 0.0) {
                val overlapSize = calculateOverlapSizeWithWall(particle, wall)
            }
            if (overlapSize > overlapLimit) {
                val relativeVelocity = particle.velocity - wall.normal * particle.velocity.dotProduct(wall.normal)

                val normalForceMagnitude =
                    -particle.Kn * overlapSize - particle.gammaN * relativeVelocity.dotProduct(wall.normal)
                val tangentialForceMagnitude = -particle.Kt * overlapSize - particle.gammaT * relativeVelocity.magnitude

                val normalForceValue = -wall.normal * normalForceMagnitude
                val tangentialForceValue = -relativeVelocity.normalize() * tangentialForceMagnitude

                wallForce += normalForceValue
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

        if (particle.id == 99) {
            return gravityForce + interactionForce + wallForce
        }

        if (wallForce != Vector() && particle.position.z < 0.0) {
            return gravityForce + interactionForce + wallForce
        }
        return gravityForce + interactionForce + wallForce
    }


}
