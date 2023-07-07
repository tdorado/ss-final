package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import engine.model.Wall
import kotlin.math.pow

class CannonballForcesCalculator() : ForcesCalculator {

    override fun getForces(particle: Particle, neighbours: List<Particle>, walls: List<Wall>): Vector {
        val g = 9.81  // Aceleración debido a la gravedad (en m/s^2)
        val gravityForce = Vector(0.0, 0.0, -particle.mass * g)  // La fuerza de gravedad actúa en la dirección -z

        var interactionForce = Vector()
        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val normalDirection = (otherParticle.position - particle.position).normalize()
                val relativeVelocity = particle.velocity - otherParticle.velocity
                val normalVelocity = normalDirection * (relativeVelocity dot normalDirection)
                val tangentialVelocity = relativeVelocity - normalVelocity

                // La fuerza de fricción es proporcional a la velocidad relativa en la dirección tangencial
                interactionForce += tangentialVelocity * particle.frictionCoefficient
            }
        }

        return gravityForce + interactionForce
    }

}