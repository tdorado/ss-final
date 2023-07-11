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
                if (particle.id == 1) {
//                    System.out.println()
                }
                if (particle.id == 2) {
//                    System.out.println()
                }
                val closestPoint = findClosestPointOnParticle(particle, otherParticle.position).roundVec()
                val overlapSize = (closestPoint - otherParticle.position).magnitude - otherParticle.radius

                val normalVector = (otherParticle.position - closestPoint).normalize()
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
        particle.collideWithWall = ""
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

                val wallKn = 1E2
                val wallKt = 2 * wallKn

                val normalForceMagnitude = -(particle.gammaN * normalVelocity) - (wallKn * overlapSize)
                val tangentialForceMagnitude =
                    -wallKt * overlapSize - (particle.gammaT) * tangentialVelocity.magnitude


                val normalForceValue = wall.normal * normalForceMagnitude
                val tangentialForceValue = tangentialVelocity.normalize() * tangentialForceMagnitude

                wallForce += -(normalForceValue + tangentialForceValue)
                particle.collideWithWall = wall.id
            } else if (wall.isParticleOverWall(particle.position, particle.radius, boxWidth, boxHeight)) {
                particle.velocity = Vector()
                return Vector()
            }
        }

        return wallForce
    }

    fun changeVelocitySignsForCollideWithWall(particle: Particle, walls: Set<Wall>, force: Vector): Vector {
//        for (wall in walls) {
//            if (wall.overlapsWithParticle(particle.position, particle.radius, boxWidth, boxHeight)) {
//                if (particle.collideWithWall == "BOTTOM") {
//                    if (particle.velocity.z < 0 && particle.position.z < particle.radius) {
//                        particle.velocity.z *= -1.0
//                    }
//                } else if (particle.collideWithWall == "BACK" || particle.collideWithWall == "FRONT") {
//                    if (particle.velocity.y < 0 && particle.position.y < particle.radius) {
//                        particle.velocity.y *= -1.0
//                    }
//                } else if (particle.collideWithWall.isNotBlank()) {
//                    if (particle.velocity.x < 0 && particle.position.x < particle.radius) {
//                        particle.velocity.x *= -1.0
//                    }
//                }
//            }
//        }
        if (particle.position.z <= particle.radius && force.z < 0.0) {
            force.z *= -1.0
        } else if (particle.position.y <= particle.radius && force.y < 0.0) {
            force.y *= -1.0
        } else if (particle.position.x < particle.radius && force.x < 0.0) {
            force.x *= -1.0
        }
        return force
//        return force.times(0.5)
    }

    //    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
//        val gravityForce = calculateGravityForce(particle)
//        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
//        var wallForce = calculateWallForce(particle, walls)
//        var totalForce = gravityForce + interactionForce + wallForce
//
//        if (particle.id == 1){
//            logger.info("Gravity force: $gravityForce, interactionForce: $interactionForce, wallForce: $wallForce, totalForce: ${totalForce}")
//        }
////        if (particle.id != 0) {
////            if (abs(particle.position.x) > 0.9 || abs(particle.position.y) > 0.9 || particle.position.z < 0.0) {
////                System.out.println("EXPLOTO")
////            }
////        }
////        return totalForce
//        return changeVelocitySignsForCollideWithWall(particle, walls, totalForce)
//    }

    private fun calculateInteractionForce(particle: Particle, otherParticle: Particle): Vector {
        val closestPoint = findClosestPointOnParticle(particle, otherParticle.position).roundVec()
        val overlapSize = (closestPoint - otherParticle.position).magnitude - otherParticle.radius

        val normalVector = (otherParticle.position - closestPoint).normalize()
        val relativeVelocity = particle.velocity - otherParticle.velocity
        val relativeTangentialVelocity = relativeVelocity.projectOnPlane(normalVector)

        val normalForceMagnitude =
            -(particle.Kn * overlapSize + particle.gammaN * relativeVelocity.dotProduct(normalVector))
        val tangentialForceMagnitude =
            -(particle.Kt * overlapSize + particle.gammaT * relativeTangentialVelocity.magnitude)

        val normalForceValue = normalVector * normalForceMagnitude
        val tangentialForceValue = relativeTangentialVelocity.normalize() * tangentialForceMagnitude

        return normalForceValue + tangentialForceValue
    }


    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        var totalForce = calculateGravityForce(particle)

        val allParticles = neighbours + walls.map { wall ->
            Particle(
                id = -1, // you can assign unique IDs for walls if needed
                position = wall.position,
                velocity = Vector(),
                radius = 0.0, // walls can be treated as particles with zero radius
                mass = Double.POSITIVE_INFINITY, // walls are immovable, so they have infinite mass
                Kt = 1E10 * 2,
                Kn = 1E10,
                gammaT = 100.0,
                gammaN = 50.0
            )
        }

        for (otherParticle in allParticles) {
            if (particle != otherParticle && particle.overlapsWith(otherParticle.position, otherParticle.radius)) {
                totalForce += calculateInteractionForce(particle, otherParticle)
            }
        }

        return totalForce
    }

}
