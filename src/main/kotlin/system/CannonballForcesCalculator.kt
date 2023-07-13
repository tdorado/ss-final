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

                    // Invierto el signo del término de amortiguación
                    val normalForceMagnitude = particle.Kn * overlapSize + particle.gammaN * relativeNormalVelocity
                    val tangentialForceMagnitude =
                        particle.Kt * overlapSize + particle.gammaT * relativeTangentVelocity.magnitude

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
            val relativePosition = particle.position - wall.position
            val distanceToWall = relativePosition.dotProduct(wall.normal)
            val overlapSize = particle.radius - distanceToWall

            if (overlapSize > overlapLimit) {

                val normalVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentialVelocity = particle.velocity - wall.normal * normalVelocity

                val normalForceMagnitude = -particle.Kn * 5.0 * overlapSize - particle.gammaN * normalVelocity
                val tangentialForceMagnitude =
                    -particle.Kt * 5.0 * overlapSize - particle.gammaT * tangentialVelocity.magnitude

                val normalForceValue = -wall.normal * normalForceMagnitude
                val tangentialForceValue = -tangentialVelocity.normalize() * tangentialForceMagnitude

                wallForce += (normalForceValue + tangentialForceValue)
            }
        }

        return wallForce
    }


    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
        val gravityForce = calculateGravityForce(particle)

        if (wallForce != Vector()) {
            return gravityForce + interactionForce + wallForce

        }
        return gravityForce + interactionForce + wallForce
    }


}
