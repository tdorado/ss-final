package system

import engine.TimeStepSimulator
import engine.integrators.BeemanIntegrator
import engine.model.Particle
import engine.model.Vector
import system.generator.CannonballParticleGenerator
import system.generator.ParticleDiameterGenerator
import kotlin.math.cos
import kotlin.math.sin

class CannonballSystem(
    val timeDelta: Double,
    val saveTimeDelta: Double,
    val cutoffTime: Double,
    val energyThreshold: Double,
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
    val pGenSave: Boolean,
    val outputFile: String,
    val pStableEnergy: Double,
    val pStableTime: Double,
    val gravity: Double,
    val shouldLog: Boolean,
) {

    fun run() {
        val boxWalls = createBoxWalls()
        val boxParticles: Set<Particle> = if (pFile.isNotBlank()) {
            Particle.loadParticlesFromFile("out/init-particles/$pFile")
        } else {
            runParticlesStabilization(boxWalls)
        }
        val cannonballParticle = createCannonBall(getHighestParticle(boxParticles))
        val particles = boxParticles + cannonballParticle
        val cannonballForcesCalculator = CannonballForcesCalculator(gravity, boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("out/runs/", outputFile, shouldLog)
        val cutCondition = KineticEnergyAndTimeCutCondition(energyThreshold, cutoffTime, shouldLog)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true, shouldLog)
    }

    private fun getHighestParticle(particle: Set<Particle>): Double {
        return (particle.maxOfOrNull { it.position.z } ?: 0.0).plus(cannonballHeight)
    }

    private fun runParticlesStabilization(boxWalls: Set<Wall>): Set<Particle> {
        val particles = createBoxParticles(boxWalls)
        if (pGenSave) {
            Particle.saveParticlesToFile(particles, "out/init-particles/particles-$outputFile", shouldLog)
        }
        val cannonballForcesCalculator = CannonballForcesCalculator(gravity, boxWalls)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles)
        val cannonballFileGenerator = CannonballFileGenerator("out/particles/", "stabilization-$outputFile", shouldLog)
        val cutCondition = KineticEnergyAndTimeCutCondition(pStableEnergy, pStableTime, shouldLog)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        val stabilizedParticles = simulator.simulate(true, shouldLog)
        if (pGenSave) {
            Particle.saveParticlesToFile(particles, "out/init-particles/stable-particles-$outputFile", shouldLog)
        }

        return stabilizedParticles
    }

    private fun createCannonBall(cannonballHeight: Double): Particle {
        val radiansAngle = Math.toRadians(cannonballAngle)
        val velocity = Vector(0.0, -cannonballVelocity * cos(radiansAngle), -cannonballVelocity * sin(radiansAngle))
        val radius = cannonballDiameter / 2
        val position = Vector(boxWidth / 2, boxWidth / 2, cannonballHeight + radius)
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
        val boxSize = Vector(boxWidth, boxWidth, boxHeight)
        val particleGenerator = CannonballParticleGenerator(
            lowParticleMass,
            boxSize,
            numberOfParticles,
            particlesDiameterGenerator,
            pKn,
            pKt,
            pGamma,
            walls
        )
        return particleGenerator.generateParticles(shouldLog)
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