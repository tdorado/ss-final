package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import kotlin.math.pow

class CannonballForcesCalculator(private val walls: Set<Wall>) :
    ForcesCalculator {

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
                if (overlapSize < 0) continue

                val relativeVelocity = particle.velocity - otherParticle.velocity
                val normalForceValue = -normalVector.times(particle.Kn * overlapSize)
                val tangentialForceValue = -relativeVelocity.times(particle.Kt * overlapSize)

                interactionForce += normalForceValue + tangentialForceValue
            }
        }

        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            if (wall.overlapsWithParticle(particle.position, particle.radius)) {
                val relativePosition = particle.position - wall.position
                val overlapSize = particle.radius - relativePosition.dotProduct(wall.normal)
                if (overlapSize < 0) continue

                val normalVelocity = particle.velocity.dotProduct(wall.normal)
                val tangentialVelocity = particle.velocity - wall.normal * normalVelocity

                val normalForceValue = wall.normal.times(particle.Kn * overlapSize)
                val tangentialForceValue = -tangentialVelocity.times(particle.Kt * overlapSize)

                wallForce += normalForceValue + tangentialForceValue
            }
        }

        // Add an upward force if the particle falls below the floor (z < 0)
        if (particle.position.z < 0) {
            val overlapSize = particle.radius - particle.position.z
            val normalForceValue = Vector(0.0, 0.0, particle.Kn * overlapSize)
            wallForce += normalForceValue
        }

        return wallForce
    }
    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)

        return gravityForce + interactionForce + wallForce
    }
}
