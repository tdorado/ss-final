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
        val g = 1.62  // Acceleration due to gravity (in m/s^2)
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
                val tangentialVelocity = particle.velocity.projectOnPlane(wall.normal)

                // Ajustar los coeficientes Kn y Kt para controlar el rebote de las partículas en las paredes
                val wallKn = 1E-8 // Coeficiente Kn para las paredes (ajustable según tus necesidades)
                val wallKt = 2 * wallKn // Coeficiente Kt para las paredes (ajustable según tus necesidades)

                val normalForceMagnitude = -wallKn * overlapSize - 1E-8 * normalVelocity
                val tangentialForceMagnitude = -wallKt * overlapSize - 1E-8 * tangentialVelocity.magnitude

                val normalForceValue = wall.normal * normalForceMagnitude
                val tangentialForceValue = tangentialVelocity.normalize() * tangentialForceMagnitude

                // Invertir la dirección de la fuerza normal para el rebote
                val invertedNormalForceValue = normalForceValue * -1.0

                wallForce += invertedNormalForceValue + tangentialForceValue

            }
        }

        return wallForce
    }


    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
        var totalForce = gravityForce + interactionForce + wallForce

        if (particle.id == 1) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] Gravity force: $gravityForce, interactionForce: $interactionForce, wallForce: $wallForce, position: ${particle.position}")
        }

        if (particle.position.z <= particle.radius) {
            // Invertir las componentes x e y de la fuerza para simular el rebote hacia arriba
            totalForce = Vector(-totalForce.x, totalForce.y, -totalForce.z)
            particle.velocity = Vector(particle.velocity.x, particle.velocity.y, abs(particle.velocity.z))
        }
        if (particle.position.y <= particle.radius && particle.position.z < boxHeight) {
            // Invertir las componentes x y z de la fuerza para simular el rebote hacia arriba
            totalForce = Vector(-totalForce.x, -totalForce.y, totalForce.z)
//            particle.velocity = Vector(particle.velocity.x, abs(particle.velocity.y), particle.velocity.z)
        }

        if (particle.position.x <= particle.radius && particle.position.z < boxHeight) {
            // Invertir las componentes y y z de la fuerza para simular el rebote hacia arriba
            totalForce = Vector(-totalForce.x, -totalForce.y, totalForce.z)
//            particle.velocity = Vector(abs(particle.velocity.x), particle.velocity.y, particle.velocity.z)

        }
        return totalForce
    }
}
