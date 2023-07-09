package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class CannonballForcesCalculator(private val walls: Set<Wall>) : ForcesCalculator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle && particle.overlapsWith(otherParticle.position, otherParticle.radius)) {
                val closestPoint = findClosestPointOnParticle(particle, otherParticle.position)
                val overlapSize = (closestPoint - otherParticle.position).magnitude - otherParticle.radius

                val normalVector = (closestPoint - otherParticle.position).normalize()
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val relativeTangentialVelocity = relativeVelocity.projectOnPlane(normalVector)

                val normalForceMagnitude =
                    -particle.Kn * overlapSize - particle.gammaN * relativeVelocity.dotProduct(normalVector)
                val tangentialForceMagnitude =
                    -particle.Kt * overlapSize - particle.gammaT * relativeTangentialVelocity.magnitude

                val normalForceValue = normalVector * normalForceMagnitude
                val tangentialForceValue = relativeTangentialVelocity.normalize() * tangentialForceMagnitude

                interactionForce += normalForceValue + tangentialForceValue
            }
        }

        return interactionForce
    }

    private fun findClosestPointOnParticle(particle: Particle, point: Vector): Vector {
        val displacement = point - particle.position
        val closestPoint = particle.position + displacement.normalize() * particle.radius
        return closestPoint
    }


    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            if (wall.overlapsWithParticle(particle.position, particle.radius)) {
                val relativePosition = particle.position - wall.position
                val overlapSize = particle.radius - relativePosition.dotProduct(wall.normal)

                val normalVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentialVelocity = particle.velocity.projectOnPlane(wall.normal)

                val normalForceMagnitude = -particle.Kn * overlapSize - particle.gammaN * normalVelocity
                val tangentialForceMagnitude =
                    -particle.Kt * overlapSize - particle.gammaT * tangentialVelocity.magnitude

                val normalForceValue = wall.normal * normalForceMagnitude
                val tangentialForceValue = tangentialVelocity.normalize() * tangentialForceMagnitude

                wallForce += normalForceValue + tangentialForceValue

                // Invertir la velocidad y aceleración si hay colisión con la pared
//                particle.velocity -= wall.normal.times(2 * normalVelocity)
                val normalVelocityComponent = particle.velocity.dotProduct(wall.normal)
                particle.velocity -= wall.normal * (2 * normalVelocityComponent)

//                particle.velocity -= Vector(0.0, 0.0, 2 * normalVelocity) * wall.normal.z
            }
        }

        return wallForce
    }


    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)

        if (particle.id == 0) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] Gravity force for cannonBall: $gravityForce, interactionForce: $interactionForce, wallForce: $wallForce, position: ${particle.position}")
        }

        return gravityForce + interactionForce + wallForce
    }
}