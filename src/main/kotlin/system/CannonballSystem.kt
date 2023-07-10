package system

import engine.TimeCutCondition
import engine.TimeStepSimulator
import engine.integrators.BeemanIntegrator
import engine.model.Particle
import engine.model.Vector
import system.particle_generators.CannonballParticleGenerator
import system.particle_generators.ParticleDiameterGenerator
import kotlin.math.sin

class CannonballSystem(
    val particleMass: Double = 0.005,
    val timeDelta: Double = 0.0003,
    val saveTimeDelta: Double = 0.0003,
    val cutoffTime: Double = 1.0,
    val boxHeight: Double = 0.25,
    val boxWidth: Double = 0.7,
    val numberOfParticles: Int = 5000,
    val minParticleDiameter: Double = 0.02,
    val maxParticleDiameter: Double = 0.03,
    val pGammaN: Double = 0.9,
    val pGammaT: Double = 0.9,
    val pKn: Double = 1E5,
    val pKt: Double = pKn / 30,
    val angle: Double = Math.toRadians(100.0)
) {
    private val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)

    // Coeficientes de fricción y restitución para la bala de cañón
    // Gamma alto implica mucho rebote
    private val cannonballGammaN = 0.9
    private val cannonballGammaT = 0.9
    private val cannonballKn = 6E-2
    private val cannonballKt = cannonballKn

    private val CONFIG = "particleMass:$particleMass" +
            "_minParticleDiameter:$minParticleDiameter" +
            "_maxParticleDiameter:$maxParticleDiameter" +
            "_bulletAngle:$angle" +
            "_pKt:$pKt" +
            "_pKn:$pKn" +
            "_pGammaN:$pGammaN" +
            "_pGammaT:$pGammaT"

    fun run(particlesFromFile: Set<Particle> = emptySet()) {
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()

        val particles: Set<Particle> = if (particlesFromFile.isEmpty()) {
            val boxParticles = createBoxParticles(boxWalls)
            boxParticles + cannonballParticle
        } else {
            particlesFromFile + cannonballParticle
        }

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, boxWidth, boxHeight)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles, boxWalls)
        val cannonballFileGenerator = CannonballFileGenerator(CONFIG)
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = 200.0
        val velocity = Vector(0.0, velocityMagnitude * sin(angle), -velocityMagnitude * sin(angle))
        val position = Vector(boxWidth / 2, (-boxWidth / 5), 3 * boxHeight)
        val radius = 175e-3 / 2
        val mass = 17.5
        return Particle(
            0,
            position,
            velocity,
            radius,
            mass,
            cannonballKn,
            cannonballKt,
            cannonballGammaT,
            cannonballGammaN
        )
    }

    private fun createBoxParticles(boxWalls: Set<Wall>): Set<Particle> {
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleDiameter, maxParticleDiameter)
        val particleGenerator = CannonballParticleGenerator(
            particleMass,
            boxSizeInMeters,
            numberOfParticles,
            boxWalls,
            particlesDiameterGenerator,
            pKn,
            pKt,
            pGammaN,
            pGammaT
        )
        return particleGenerator.generateParticles()
    }

    fun createBoxWalls(): Set<Wall> {
        return setOf(
            // Left wall: se sitúa en el punto (0,0,0) y su normal apunta hacia la derecha (1,0,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), "LEFT"),

            // Right wall: se sitúa en el punto (boxWidth, 0, 0) y su normal apunta hacia la izquierda (-1,0,0).
            Wall(Vector(boxWidth, 0.0, 0.0), Vector(-1.0, 0.0, 0.0), "RIGHT"),

            // Front wall: se sitúa en el punto (0,0,0) y su normal apunta hacia atrás (0,1,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), "FRONT"),

            // Back wall: se sitúa en el punto (0, boxWidth, 0) y su normal apunta hacia adelante (0,-1,0).
            Wall(Vector(0.0, boxWidth, 0.0), Vector(0.0, -1.0, 0.0), "BACK"),

            // Bottom wall: se sitúa en el punto (0,0,0) y su normal apunta hacia arriba (0,0,1). Esta es la base de la caja.
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), "BOTTOM"),
        )
    }
}