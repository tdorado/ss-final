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
        const val particleMass = 0.05
        const val timeDelta = 0.00001
        const val saveTimeDelta = 0.00005
        const val cutoffTime = 0.02
        private const val boxHeight = 1.0
        private const val boxWidth = 1.0
        val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)
        const val numberOfParticles = 3000
        private const val minParticleDiameter = 0.01
        private const val maxParticleDiameter = 0.05
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleDiameter, maxParticleDiameter)
        const val Kt = 2.2E6
        const val Kn = Kt / 25
        const val wallsFrictionCoefficient = 1.0
    }

    fun run(particlesFromFile: Set<Particle> = emptySet()) {
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()

        val particles: Set<Particle> = if (particlesFromFile.isEmpty()) {
            val boxParticles = createBoxParticles(boxWalls)
            setOf(cannonballParticle) + boxParticles
        } else {
            setOf(cannonballParticle) + particlesFromFile
        }

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles, boxWalls)
        val cannonballFileGenerator = CannonballFileGenerator("cannonball-" + String.format("%.6f", timeDelta))
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = 450.0
        val angle = Math.PI / 2
        val velocity = Vector(0.0, 0.0, -velocityMagnitude * sin(angle))
        val position = Vector(boxWidth / 2.0, boxWidth / 2.0, 2 * boxHeight)
        val radius = 175e-3 / 2
        val mass = 17.5
        val frictionCoefficient = 0.15
        return Particle(0, position, velocity, radius, mass, Kn, Kt)
    }


    private fun createBoxParticles(boxWalls: Set<Wall>): Set<Particle> {
        val particleGenerator = CannonballParticleGenerator(
            particleMass,
            boxSizeInMeters,
            numberOfParticles,
            boxWalls,
            particlesDiameterGenerator,
            Kn,
            Kt,
        )
        return particleGenerator.generateParticles()
    }

    fun createBoxWalls(): Set<Wall> {
        return setOf(
            // Left wall: se sitúa en el punto (0,0,0) y su normal apunta hacia la derecha (1,0,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), wallsFrictionCoefficient, "LEFT"),

            // Right wall: se sitúa en el punto (boxWidth, 0, 0) y su normal apunta hacia la izquierda (-1,0,0).
            Wall(Vector(boxWidth, 0.0, 0.0), Vector(-1.0, 0.0, 0.0), wallsFrictionCoefficient, "RIGHT"),

            // Front wall: se sitúa en el punto (0,0,0) y su normal apunta hacia atrás (0,1,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), wallsFrictionCoefficient, "FRONT"),

            // Back wall: se sitúa en el punto (0, boxWidth, 0) y su normal apunta hacia adelante (0,-1,0).
            Wall(Vector(0.0, boxWidth, 0.0), Vector(0.0, -1.0, 0.0), wallsFrictionCoefficient, "BACK"),

            // Bottom wall: se sitúa en el punto (0,0,0) y su normal apunta hacia arriba (0,0,1). Esta es la base de la caja.
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), wallsFrictionCoefficient, "BOTTOM"),
        )
    }
}