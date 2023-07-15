package system

import engine.KineticEnergyCondition
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
    val timeDelta: Double = 0.0001,
    val saveTimeDelta: Double = 0.0003,
    val cutoffTime: Double = 1.5,
    val boxHeight: Double = 1.0,
    val boxWidth: Double = 0.4,
    val numberOfParticles: Int = 1000,
    val minParticleDiameter: Double = 0.015,
    val maxParticleDiameter: Double = 0.03,
    val particleMass: Double = 0.085,
    val pKn: Double = 2E6,
    val pKt: Double = 2 * pKn,
    val pGamma: Double = 70.0,
    val wallKn: Double = 6E2,
    val wallKt: Double = 2 * wallKn,
    val wallGamma: Double = 20.0,
    val cannonballKn: Double = pKn,
    val cannonballKt: Double = 2 * cannonballKn,
    val cannonballGamma: Double = pGamma,
    val cannonballAngle: Double = Math.toRadians(90.0),
    val cannonballVelocity: Double = 30.0,
    val cannonballMass: Double = 17.5,
    val cannonballRadius: Double = 175e-3 / 2,
    val pFile: String = "",
) {
    private val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)

    private val CONFIG = "nP:$numberOfParticles" +
            "_pMass:$particleMass" +
            "_minDiameter:$minParticleDiameter" +
            "_maxDiameter:$maxParticleDiameter" +
            "_angle:$cannonballAngle" +
            "_pKt:$pKt" +
            "_pKn:$pKn" +
            "_pGamma:$pGamma" +
            "_cutoffTime:$cutoffTime" +
            "_TimeDelta:$saveTimeDelta" +
            "_timeDelta:$timeDelta" +
            "_wallGamma${wallGamma}" +
            "_wallKn${wallKn}_parallel"

    fun run() {
        val saveParticles = pFile == null
        var boxParticles: Set<Particle>
        if (pFile.isNotBlank()) {
            boxParticles = Particle.loadParticlesFromFile("out/$pFile")
        } else {
            boxParticles = runParticlesStabilization()
            Particle.saveParticlesToFile(boxParticles, "PARTICLES_$CONFIG")
        }
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()


        val particles: Set<Particle> = boxParticles + cannonballParticle

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, maxParticleDiameter)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator(CONFIG)
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
        Particle.saveParticlesToFile(particles.map { it.resetParticle() }.toSet(), CONFIG + "FIRST_POSITION")
    }

    fun runParticlesStabilization(): Set<Particle> {
        val boxWalls = createBoxWalls()
        val boxParticles = createBoxParticles(boxWalls)

        val particles: Set<Particle> = boxParticles

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, maxParticleDiameter)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("Stabilization_dt$CONFIG")
        val cutCondition = KineticEnergyCondition()
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        return simulator.waitForParticlesToStabilize()
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