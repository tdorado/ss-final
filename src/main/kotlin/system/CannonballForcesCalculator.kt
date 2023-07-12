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

                val normalVector = (otherParticle.position - particle.position).normalize()
                val overlapSize =
                    (particle.radius + otherParticle.radius) - (otherParticle.position - particle.position).magnitude

                // Add position correction to avoid overlap
                val positionCorrection = normalVector * overlapSize * 0.5
                particle.position -= positionCorrection

                if (overlapSize < 0) continue

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
            if (wall.overlapsWithParticle(particle, boxWidth, boxHeight)) {
                val relativePosition = particle.position - wall.position
                val overlapSize = particle.radius - relativePosition.dotProduct(wall.normal)
                if (particle.position.z < particle.radius){
                    particle.position.z += overlapSize
                }
                if (overlapSize < 0) continue

                val normalVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentialVelocity = particle.velocity - wall.normal * normalVelocity

                val normalForceMagnitude = particle.Kn * overlapSize - particle.gammaN * normalVelocity
                val tangentialForceMagnitude = particle.Kt * overlapSize - particle.gammaT * tangentialVelocity.magnitude

                val normalForceValue = -wall.normal.times(normalForceMagnitude)
                val tangentialForceValue = -tangentialVelocity.normalize().times(tangentialForceMagnitude)

                wallForce -= (normalForceValue + tangentialForceValue)
            }
        }

        return wallForce
    }

    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
//        if (wallForce != Vector()){
//            return wallForce + gravityForce
//        }

        return gravityForce + interactionForce + wallForce
    }


}
