package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import kotlin.math.pow

class CannonballForcesCalculator(private val walls: List<Wall>, private val boxHeight: Double = 1.0) : ForcesCalculator {

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81  // Acceleration due to gravity (in m/s^2)
        return Vector(0.0, 0.0, -particle.mass * g)  // Gravity force acts in the -z direction
    }

    // Ojo con esto, calcula la presion teniendo en cuenta la posicion de la particula en el cajon que deberia ser lo correcto
    private fun calculatePressure(particle: Particle, boxHeight: Double): Double {
        // We calculate the pressure as the weight of the particle times the height in the box
        // divided by the surface area of the particle.
        val weight = particle.mass * 9.81 // Weight = mass * g
        val heightRatio = (boxHeight - particle.position.z) / boxHeight
        return weight * heightRatio / (2 * Math.PI * particle.radius.pow(2.0))
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: List<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val normalDirection = (otherParticle.position - particle.position).normalize()
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val normalVelocity = normalDirection * (relativeVelocity.dotProduct(normalDirection))
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
                val normalDirection = wall.normal
                val normalVelocity = normalDirection * (particle.velocity.dotProduct(normalDirection))

                // The wall applies a force that is proportional to the particle's velocity in the direction of the wall's normal, also the -2 is used to simulate a rebound, -1 if we only want to invert sign
                wallForce += normalVelocity.times(-2.0) * particle.frictionCoefficient
            }
        }

        return wallForce
    }

    override fun getForces(particle: Particle, neighbours: List<Particle>): Vector {
        val gravityForce = calculateGravityForce(particle)
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)

        // Calculate and set the pressure for the particle
        particle.pressure = calculatePressure(particle, boxHeight)

        return gravityForce + interactionForce + wallForce
    }


}