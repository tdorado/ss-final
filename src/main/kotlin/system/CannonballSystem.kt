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
    val timeDelta: Double,
    val saveTimeDelta: Double,
    val cutoffTime: Double,
    val boxHeight: Double,
    val boxWidth: Double,
    val numberOfParticles: Int,
    val minParticleDiameter: Double,
    val maxParticleDiameter: Double,
    val lowParticleMass: Double,
    val pKn: Double,
    val pKt: Double,
    val pGamma: Double,
    val wallKn: Double,
    val wallKt: Double,
    val wallGamma: Double,
    val cannonballKn: Double,
    val cannonballKt: Double,
    val cannonballGamma: Double,
    val cannonballAngle: Double,
    val cannonballVelocity: Double,
    val cannonballMass: Double,
    val cannonballDiameter: Double,
    val cannonballHeight: Double,
    val pFile: String,
    val outputFile: String,
    val pStableEnergy: Double,
    val pStableTime: Double,
) {
    private val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)

    fun run() {
        val boxParticles: Set<Particle> = if (pFile.isNotBlank()) {
            Particle.loadParticlesFromFile("out/init-particles/$pFile")
        } else {
            runParticlesStabilization()
        }
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()
        val particles: Set<Particle> = boxParticles + cannonballParticle
        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("out/runs", outputFile)
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun runParticlesStabilization(): Set<Particle> {
        val boxWalls = createBoxWalls()
        val particles = createBoxParticles(boxWalls)
        Particle.saveParticlesToFile(particles, "out/init-particles/particles-$outputFile")
        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("out/particles", "stabilization-$outputFile")
        val cutCondition = KineticEnergyAndTimeCutCondition(pStableEnergy, pStableTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        return simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val radiansAngle = Math.toRadians(cannonballAngle)
        val velocity = Vector(0.0, -cannonballVelocity * cos(radiansAngle), -cannonballVelocity * sin(radiansAngle))
        val position = Vector(boxWidth / 2, boxWidth / 2, cannonballHeight)
        val radius = cannonballDiameter / 2
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
            lowParticleMass,
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