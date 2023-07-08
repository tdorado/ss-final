package system

import engine.TimeCutCondition
import engine.TimeStepSimulator
import engine.integrators.BeemanIntegrator
import engine.model.Particle
import engine.model.Vector
import kotlin.math.cos
import kotlin.math.sin


class CannonballSystem {
    companion object {
        const val particleMass = 0.01
        const val timeDelta = 0.01
        const val saveTimeDelta = 0.02
        const val cutoffTime = 10.0
        const val particlesMinRadius = 0.02
        const val particlesMaxRadius = 0.05
        val boxSize = Vector(1.0, 1.0, 1.0)
        const val numberOfParticles = 100
        const val boxSizeInMeters = 1.0
        private const val minParticleMass = 0.01
        private const val maxParticleMass = 0.05
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleMass, maxParticleMass)
        const val boxParticlesFrictionCoefficient = 0.55
    }

    fun run() {
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()
        val boxParticles = createBoxParticles(boxWalls)
        val particles = boxParticles + cannonballParticle
        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, boxSizeInMeters)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("cannonball-" + String.format("%.6f", timeDelta))
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = 450.0
        val angle = Math.PI / 4
        val velocity = Vector(velocityMagnitude * cos(angle), velocityMagnitude * sin(angle), 0.0)
        val position = Vector(0.0, 0.0, 0.0)
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

    private fun createBoxWalls(): List<Wall> {
        return listOf(
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(boxSizeInMeters, 0.0, 0.0), Vector(1.0, 0.0, 0.0), boxSizeInMeters, boxSizeInMeters),
            Wall(Vector(0.0, boxSizeInMeters, 0.0), Vector(0.0, 1.0, 0.0), boxSizeInMeters, boxSizeInMeters),
        )
    }
}