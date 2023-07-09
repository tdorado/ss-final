package system

import engine.TimeCutCondition
import engine.TimeStepSimulator
import engine.integrators.BeemanIntegrator
import engine.model.Particle
import engine.model.Vector
import system.particle_generators.CannonballParticleGenerator
import system.particle_generators.ParticleDiameterGenerator
import kotlin.math.sin


class CannonballSystem {
    companion object {
        const val particleMass = 0.5
        const val timeDelta = 0.05
        const val saveTimeDelta = 0.02
        const val cutoffTime = 1.0
        const val particlesMinRadius = 0.02
        const val particlesMaxRadius = 0.05
        const val boxSizeInMeters = 0.5
        val boxSize = Vector(boxSizeInMeters, boxSizeInMeters, boxSizeInMeters)
        const val numberOfParticles = 5000
        private const val minParticleDiameter = 0.01
        private const val maxParticleDiameter = 0.05
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleDiameter, maxParticleDiameter)
        const val boxParticlesFrictionCoefficient = 100.0
    }

    fun run(particlesFromFile: List<Particle> = emptyList()) {
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()

        val particles: List<Particle> = if (particlesFromFile.isEmpty()) {
            val boxParticles = createBoxParticles(boxWalls)
            listOf(cannonballParticle) + boxParticles
        } else {
            listOf(cannonballParticle) + particlesFromFile
        }

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, boxSizeInMeters)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles, boxWalls)
        val cannonballFileGenerator = CannonballFileGenerator("cannonball-" + String.format("%.6f", timeDelta))
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = 3.0
        val angle = Math.PI / 2
        val velocity = Vector(0.0, 0.0, -velocityMagnitude * sin(angle))
        val position = Vector(boxSizeInMeters / 2.0, boxSizeInMeters / 2.0, 2 * boxSizeInMeters)
        val radius = 175e-3 / 2
        val mass = 17.5
        val frictionCoefficient = 0.15
        return Particle(0, position, velocity, radius, mass, frictionCoefficient, 0.0)
    }

    private fun createBoxParticles(boxWalls: List<Wall>): List<Particle> {
        val particleGenerator = CannonballParticleGenerator(
            particleMass,
            particlesMinRadius,
            particlesMaxRadius,
            boxSize,
            numberOfParticles,
            boxWalls,
            particlesDiameterGenerator,
            0.0,
            boxParticlesFrictionCoefficient,
        )
        return particleGenerator.generateParticles()
    }

    public fun createBoxWalls(): List<Wall> {
        return listOf(
            // Left wall: se sitúa en el punto (0,0,0) y su normal apunta hacia la derecha (1,0,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters, "LEFT"),

            // Right wall: se sitúa en el punto (boxSizeInMeters, 0, 0) y su normal apunta hacia la izquierda (-1,0,0).
            Wall(Vector(boxSizeInMeters, 0.0, 0.0), Vector(-1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters, "RIGHT"),

            // Front wall: se sitúa en el punto (0,0,0) y su normal apunta hacia atrás (0,1,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), boxSizeInMeters, boxSizeInMeters, "FRONT"),

            // Back wall: se sitúa en el punto (0, boxSizeInMeters, 0) y su normal apunta hacia adelante (0,-1,0).
            Wall(Vector(0.0, boxSizeInMeters, 0.0), Vector(0.0, -1.0, 0.0), boxSizeInMeters, boxSizeInMeters, "BACK"),

            // Bottom wall: se sitúa en el punto (0,0,0) y su normal apunta hacia arriba (0,0,1). Esta es la base de la caja.
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), boxSizeInMeters, boxSizeInMeters, "BOTTOM"),
        )
    }
}