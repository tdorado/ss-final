package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import engine.model.Wall
import kotlin.math.pow

class CannonballForcesCalculator() : ForcesCalculator {

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: List<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val normalDirection = (otherParticle.position - particle.position).normalize()
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val normalVelocity = normalDirection * (relativeVelocity dot normalDirection)
                val tangentialVelocity = relativeVelocity - normalVelocity

                // Friction force is proportional to the relative velocity in the tangential direction
                interactionForce += tangentialVelocity * -particle.frictionCoefficient
            }
        }

        return interactionForce
    }

    private fun calculateWallForce(particle: Particle, walls: List<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            if (wall.overlapsWith(particle.position, particle.radius)) {
                val normalDirection = wall.getNormal()
                val normalVelocity = normalDirection * (particle.velocity dot normalDirection)

                // The wall applies a force that is proportional to the particle's velocity in the direction of the wall's normal, also the -2 is used to simulate a rebound, -1 if we only want to invert sign
                wallForce += normalVelocity.times(-2.0) * particle.frictionCoefficient
            }
        }

        return wallForce
    }

    override fun getForces(particle: Particle, neighbours: List<Particle>, walls: List<Wall>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)

        return gravityForce + interactionForce + wallForce
    }


}