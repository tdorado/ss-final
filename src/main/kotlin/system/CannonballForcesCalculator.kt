package system

import engine.ForcesCalculator
import engine.model.Particle
import engine.model.Vector
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import kotlin.math.floor

class CannonballForcesCalculator(private val walls: Set<Wall>, private val maxParticleDiameter: Double) : ForcesCalculator {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun calculateGravityForce(particle: Particle): Vector {
        val g = 9.81
        return Vector(0.0, 0.0, -particle.mass * g)
    }

    private fun calculateParticleInteractionForce(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                val overlapSize = particle.overlapSize(otherParticle.position, otherParticle.radius)
                if (overlapSize > 0.0) {
                    val relativePosition = otherParticle.position - particle.position
                    val normalVector = relativePosition.normalize()

                    val relativeVelocity = particle.velocity - otherParticle.velocity
                    val relativeNormalVelocity = relativeVelocity.dotProduct(normalVector)
                    val relativeTangentVelocity = relativeVelocity - normalVector * relativeNormalVelocity

                    val normalForceMagnitude = particle.Kn * overlapSize + particle.gamma * relativeNormalVelocity
                    val tangentialForceMagnitude = particle.Kt * overlapSize

                    val normalForceValue = -normalVector * normalForceMagnitude
                    val tangentialForceValue = -relativeTangentVelocity.normalize() * tangentialForceMagnitude

                    interactionForce += normalForceValue + tangentialForceValue
                }
            }
        }
        return interactionForce
    }
    private fun calculateWallForce(particle: Particle, walls: Set<Wall>): Vector {
        var wallForce = Vector()

        for (wall in walls) {
            val overlapSize = calculateOverlapSizeWithWall(particle, wall)
            if (overlapSize > 0.0) {
                val relativeVelocity = particle.velocity - wall.normal * particle.velocity.dotProduct(wall.normal)

                val normalForceMagnitude =
                    -wall.Kn * overlapSize - wall.gamma * relativeVelocity.dotProduct(wall.normal)
                val tangentialForceMagnitude = wall.Kt * overlapSize

                val normalForceValue = -wall.normal * normalForceMagnitude
                val tangentialForceValue = -relativeVelocity.normalize() * tangentialForceMagnitude

                wallForce += normalForceValue + tangentialForceValue
            }
        }

        return wallForce
    }

    private fun calculateParticleInteractionForceCellIndex(particle: Particle, neighbours: Set<Particle>): Vector {
        var interactionForce = Vector()
        // Definir el tamaño de la celda basado en el radio máximo de las partículas.
        val cellSize = 2.0 * maxParticleDiameter
        // Crear una malla 3D de celdas.
        val cells = hashMapOf<Vector, MutableList<Particle>>()

        for (otherParticle in neighbours) {
            if (particle != otherParticle) {
                // Asignar cada partícula a una celda.
                val cellIndex = Vector(
                    floor(otherParticle.position.x / cellSize),
                    floor(otherParticle.position.y / cellSize),
                    floor(otherParticle.position.z / cellSize)
                )
                cells.getOrPut(cellIndex) { mutableListOf() }.add(otherParticle)
            }
        }

        // Calcular las fuerzas solo para partículas en la misma celda o en celdas adyacentes.
        val cellIndex = Vector(
            floor(particle.position.x / cellSize),
            floor(particle.position.y / cellSize),
            floor(particle.position.z / cellSize)
        )

        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    val neighbourCellIndex = Vector(cellIndex.x + dx, cellIndex.y + dy, cellIndex.z + dz)
                    val cellParticles = cells[neighbourCellIndex] ?: continue

                    for (otherParticle in cellParticles) {
                        val overlapSize = particle.overlapSize(otherParticle.position, otherParticle.radius)
                        if (overlapSize > 0.0) {
                            val relativePosition = otherParticle.position - particle.position
                            val normalVector = relativePosition.normalize()

                            val relativeVelocity = particle.velocity - otherParticle.velocity
                            val relativeNormalVelocity = relativeVelocity.dotProduct(normalVector)
                            val relativeTangentVelocity = relativeVelocity - normalVector * relativeNormalVelocity

                            val normalForceMagnitude = particle.Kn * overlapSize + particle.gamma * relativeNormalVelocity
                            val tangentialForceMagnitude = particle.Kt * overlapSize

                            val normalForceValue = -normalVector * normalForceMagnitude
                            val tangentialForceValue = -relativeTangentVelocity.normalize() * tangentialForceMagnitude

                            interactionForce += normalForceValue + tangentialForceValue
                        }
                    }
                }
            }
        }

        return interactionForce
    }

    private fun calculateOverlapSizeWithWall(particle: Particle, wall: Wall): Double {
        val distanceToWall = (particle.position - wall.position).dotProduct(wall.normal)
        return particle.radius - distanceToWall
    }

    override fun getForces(particle: Particle, neighbours: Set<Particle>): Vector {
        val interactionForce = calculateParticleInteractionForce(particle, neighbours)
        val wallForce = calculateWallForce(particle, walls)
        val gravityForce = calculateGravityForce(particle)
        return gravityForce + interactionForce + wallForce
    }
}
