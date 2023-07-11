package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CannonballForcesCalculator(private val walls: Set<Wall>, val boxWidth: Double, val boxHeight: Double) :
    ForcesCalculator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81 // Acceleration due to gravity (in m/s^2)
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
                    -(particle.Kn * overlapSize + particle.gammaN * relativeVelocity.dotProduct(normalVector))
                val tangentialForceMagnitude =
                    -(particle.Kt * overlapSize + particle.gammaT * relativeTangentialVelocity.magnitude)

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
            if (wall.overlapsWithParticle(particle.position, particle.radius, boxWidth, boxHeight)) {
                val relativePosition = particle.position - wall.position
                val overlapSize = particle.radius - relativePosition.dotProduct(wall.normal)

                val normalVelocity = particle.velocity.dotProduct(wall.normal)

                val tangentialVelocity = if (particle.velocity.isParallelTo(wall.normal)) {
                    Vector()
                } else {
                    particle.velocity.projectOnPlane(wall.normal)
                }

                val wallKn = 5E2
                val wallKt = 2 * wallKn

                val normalForceMagnitude = -(particle.gammaN * normalVelocity) - (wallKn * overlapSize)
                val tangentialForceMagnitude =
                    -wallKt * overlapSize - (particle.gammaT) * tangentialVelocity.magnitude


                val normalForceValue = wall.normal * normalForceMagnitude
                val tangentialForceValue = tangentialVelocity.normalize() * tangentialForceMagnitude

                wallForce += -(normalForceValue + tangentialForceValue)
                particle.collideWithWall = wall.id
            }
        }

        return wallForce
    }

    fun changeVelocitySignsForCollideWithWall(particle: Particle, walls: Set<Wall>, force: Vector): Vector {
        for (wall in walls) {
            if (wall.overlapsWithParticle(particle.position, particle.radius, boxWidth, boxHeight)) {
                if (particle.collideWithWall == "BOTTOM") {
                    if (particle.velocity.z < 0 && particle.position.z < particle.radius) {
                        particle.velocity.z *= -0.01
                        force.z *= 0.01
                    }
                } else if (particle.collideWithWall == "BACK" || particle.collideWithWall == "FRONT") {
                    if (particle.velocity.y < 0 && particle.position.y < particle.radius) {
                        particle.velocity.y *= -0.01
                        force.y *= 0.01
                    }
                } else if (particle.collideWithWall.isNotBlank()) {
                    if (particle.velocity.x < 0 && particle.position.x < particle.radius) {
                        particle.velocity.x *= -0.01
                        force.x *= 0.01
                    }
                }
            }
        }
        return force
    }

    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        var wallForce = calculateWallForce(particle, walls)
        var totalForce = gravityForce + interactionForce + wallForce
        return changeVelocitySignsForCollideWithWall(particle, walls, totalForce)

    }
}
