package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs

class CannonballForcesCalculator(private val walls: Set<Wall>, val boxWidth: Double, val boxHeight: Double) :
    ForcesCalculator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle && particle.overlapsWith(otherParticle.position, otherParticle.radius)) {

                val relativePosition = otherParticle.position - particle.position
                val overlapSize = (particle.radius + otherParticle.radius) - relativePosition.magnitude
                val normalVector = relativePosition.normalize()
                if (overlapSize > 0) {
                    // Corrección de posición para evitar superposición
                    val positionCorrection = normalVector * (overlapSize * 0.5)
                    particle.position -= positionCorrection
                    otherParticle.position += positionCorrection
                }
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val relativeNormalVelocity = relativeVelocity.dotProduct(normalVector)
                val relativeTangentVelocity = relativeVelocity - normalVector.times(relativeNormalVelocity)

                // Invierto el signo del término de amortiguación
                val normalForceMagnitude = particle.Kn * overlapSize + particle.gammaN * relativeNormalVelocity
                val tangentialForceMagnitude = particle.Kt * overlapSize + particle.gammaT * relativeTangentVelocity.magnitude

                val normalForceValue = -normalVector.times(normalForceMagnitude)
                val tangentialForceValue = -relativeTangentVelocity.normalize().times(tangentialForceMagnitude)

                interactionForce += normalForceValue + tangentialForceValue
            }
        }


        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            if(wall.id == "BOTTOM") {
                if (particle.position.z < particle.radius) {
                    val overlapSize = particle.radius - particle.position.z
                    particle.position.z += overlapSize

                    val normalVelocity = particle.velocity.z
                    val tangentialVelocity = Vector(particle.velocity.x, particle.velocity.y, 0.0)

                    val normalForceMagnitude = - particle.gammaN * normalVelocity
                    val tangentialForceMagnitude = - particle.gammaT * tangentialVelocity.magnitude

                    val normalForceValue = Vector(0.0, 0.0, -normalForceMagnitude)
                    val tangentialForceValue = -tangentialVelocity.normalize().times(tangentialForceMagnitude)

                    wallForce -= (normalForceValue + tangentialForceValue)
                }
            } else {
                val relativePosition = particle.position - wall.position
                val distanceToWall = relativePosition.dotProduct(wall.normal)

                if (distanceToWall < particle.radius) {
                    val overlapSize = particle.radius - distanceToWall
                    particle.position += wall.normal * overlapSize

                    val normalVelocity = particle.velocity.dotProduct(wall.normal)
                    val tangentialVelocity = particle.velocity - wall.normal * normalVelocity

                    val normalForceMagnitude = - particle.gammaN * normalVelocity
                    val tangentialForceMagnitude = - particle.gammaT * tangentialVelocity.magnitude

                    val normalForceValue = -wall.normal.times(normalForceMagnitude)
                    val tangentialForceValue = -tangentialVelocity.normalize().times(tangentialForceMagnitude)

                    wallForce -= (normalForceValue + tangentialForceValue)
                }
            }
        }


        return wallForce
    }

    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
        if (wallForce != Vector()){
            return wallForce + gravityForce
        }

        if (particle.id == 1 || particle.id == 2){
            logger.info("GravityForce $gravityForce InteractionForce $interactionForce wallForce $wallForce")
        }

        return gravityForce + interactionForce + wallForce
    }


}
