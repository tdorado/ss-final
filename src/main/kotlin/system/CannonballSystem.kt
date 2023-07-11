package system

import engine.TimeCutCondition
import engine.TimeStepSimulator
import engine.integrators.BeemanIntegrator
import engine.model.Particle
import engine.model.Vector
import system.particle_generators.CannonballParticleGenerator
import system.particle_generators.ParticleDiameterGenerator
import kotlin.math.cos
import kotlin.math.sin

class CannonballSystem(
    val particleMass: Double = 0.005,
    val timeDelta: Double = 0.0005,
    val saveTimeDelta: Double = 0.0005,
    val cutoffTime: Double = 1.0,
    val boxHeight: Double = 0.25,
    val boxWidth: Double = 0.9,
    val numberOfParticles: Int = 10000,
    val minParticleDiameter: Double = 0.02,
    val maxParticleDiameter: Double = 0.03,
    val pGammaN: Double = 0.9,
    val pGammaT: Double = pGammaN,
    val pKn: Double = 5E4,
    val pKt: Double = pKn,
    val angle: Double = Math.toRadians(90.0)
) {
    private val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)

    // Gamma alto implica mucho rebote
    private val cannonballGammaN = pGammaN
    private val cannonballGammaT = pGammaT
    private val cannonballKn = pKn
    private val cannonballKt = 2 * cannonballKn

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
        val onlyCannon = false
        val particles: Set<Particle> = if (onlyCannon) {
            setOf(cannonballParticle)
        } else {
            val boxParticles = createBoxParticles(boxWalls)
            boxParticles + cannonballParticle
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
        val velocityMagnitude = 2.0
        val velocity = Vector(0.0, -velocityMagnitude * cos(angle), -velocityMagnitude * sin(angle))
        val position = Vector(boxWidth / 2, boxWidth / 3, 2 * boxHeight)
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