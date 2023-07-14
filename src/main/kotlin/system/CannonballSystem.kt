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
    val timeDelta: Double = 0.00002,
    val saveTimeDelta: Double = 0.001,
    val cutoffTime: Double = 0.5,
    val boxHeight: Double = 1.0,
    val boxWidth: Double = 0.445,
    val numberOfParticles: Int = 6000,
    val minParticleDiameter: Double = 0.015,
    val maxParticleDiameter: Double = 0.025,
    val particleMass: Double = 0.025,
    val pKn: Double = 1E5,
    val pKt: Double = 2 * pKn,
    val pGamma: Double = 6.0,
    val wallKn: Double = 3E3,
    val wallKt: Double = 2 * wallKn,
    val wallGamma: Double = 6.0,
    val cannonballKn: Double = pKn,
    val cannonballKt: Double = 2 * cannonballKn,
    val cannonballGamma: Double = pGamma,
    val cannonballAngle: Double = Math.toRadians(90.0),
    val cannonballVelocity: Double = 20.0,
    val cannonballMass: Double = 17.5,
    val cannonballRadius: Double = 175e-3 / 2,
) {
    private val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)

    private val CONFIG = "numberOfParticles:$numberOfParticles" +
            "_particleMass:$particleMass" +
            "_minParticleDiameter:$minParticleDiameter" +
            "_maxParticleDiameter:$maxParticleDiameter" +
            "_cannonballAngle:$cannonballAngle" +
            "_pKt:$pKt" +
            "_pKn:$pKn" +
            "_pGamma:$pGamma" +
            "_cutoffTime:$cutoffTime" +
            "_saveTimeDelta:$saveTimeDelta" +
            "_timeDelta:$timeDelta"

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

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator(CONFIG)
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = cannonballVelocity
        val velocity = Vector(0.0, -velocityMagnitude * cos(cannonballAngle), -velocityMagnitude * sin(cannonballAngle))
        val position = Vector(boxWidth / 2, boxWidth / 2, 0.6)
        val radius = cannonballRadius
        val mass = cannonballMass
        return Particle(
            0,
            position,
            velocity,
            radius,
            mass,
            cannonballKn,
            cannonballKt,
            cannonballGamma,
        )
    }

    private fun createBoxParticles(walls: Set<Wall>): Set<Particle> {
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleDiameter, maxParticleDiameter)
        val particleGenerator = CannonballParticleGenerator(
            particleMass,
            boxSizeInMeters,
            numberOfParticles,
            particlesDiameterGenerator,
            pKn,
            pKt,
            pGamma,
            walls
        )
        return particleGenerator.generateParticles()
    }

    private fun createBoxWalls(): Set<Wall> {
        return setOf(
            // Left wall: se sitúa en el punto (0,0,0) y su normal apunta hacia la derecha (1,0,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), wallKn, wallKt, wallGamma, "LEFT"),

            // Right wall: se sitúa en el punto (boxWidth, 0, 0) y su normal apunta hacia la izquierda (-1,0,0).
            Wall(Vector(boxWidth, 0.0, 0.0), Vector(-1.0, 0.0, 0.0), wallKn, wallKt, wallGamma, "RIGHT"),

            // Front wall: se sitúa en el punto (0,0,0) y su normal apunta hacia atrás (0,1,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), wallKn, wallKt, wallGamma, "FRONT"),

            // Back wall: se sitúa en el punto (0, boxWidth, 0) y su normal apunta hacia adelante (0,-1,0).
            Wall(Vector(0.0, boxWidth, 0.0), Vector(0.0, -1.0, 0.0), wallKn, wallKt, wallGamma, "BACK"),

            // Bottom wall: se sitúa en el punto (0,0,0) y su normal apunta hacia arriba (0,0,1). Esta es la base de la caja.
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), wallKn, wallKt, wallGamma, "BOTTOM"),
        )
    }
}