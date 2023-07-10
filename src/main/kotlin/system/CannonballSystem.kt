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
        const val particleMass = 0.008
        const val timeDelta = 0.00005
        const val saveTimeDelta = 0.00005
        const val cutoffTime = 3.0
        private const val boxHeight = 0.5
        private const val boxWidth = 1.0
        val boxSizeInMeters = Vector(boxWidth, boxWidth, boxHeight)
        const val numberOfParticles = 10000
        private const val minParticleDiameter = 0.01
        private const val maxParticleDiameter = 0.03
        val particlesDiameterGenerator = ParticleDiameterGenerator(minParticleDiameter, maxParticleDiameter)

        // Coeficientes de fricción y restitución para la bala de cañón
        // Gamma alto implica mucho rebote
        const val cannonballGammaN = 0.9
        const val cannonballGammaT = 0.9
        const val cannonballKt = 1E-5
        const val cannonballKn = cannonballKt

        // Coeficientes de fricción y restitución para las partículas del lecho
        const val pGammaN = 0.9
        const val pGammaT = 0.9
        const val pKt = 1E6
        const val pKn = pKt

        // Coeficientes de fricción y restitución para las paredes
        const val wGammaN = 0.5
        const val wGammaT = 0.5
        const val wKt = 5E1
        const val wKn = wKt / 25
    }

    //    Kn es el coeficiente elástico en la dirección normal, que es la dirección perpendicular a la superficie de contacto. Un Kn más alto significa que hay una mayor resistencia a la superposición de partículas o a la superposición de una partícula con una pared en esta dirección. Esto podría resultar en un rebote más fuerte cuando ocurre una colisión.
//    Kt es el coeficiente elástico en la dirección tangencial, que es paralela a la superficie de contacto. Un Kt más alto significa que hay una mayor resistencia a la superposición de partículas o a la superposición de una partícula con una pared en esta dirección. Esto podría resultar en una mayor resistencia al deslizamiento de una partícula a lo largo de otra o a lo largo de una pared.
    fun run(particlesFromFile: Set<Particle> = emptySet()) {
        val cannonballParticle = createCannonBall()
        val boxWalls = createBoxWalls()

        val particles: Set<Particle> = if (particlesFromFile.isEmpty()) {
            val boxParticles = createBoxParticles(boxWalls)
            setOf(cannonballParticle) + boxParticles
        } else {
            setOf(cannonballParticle) + particlesFromFile
        }

        val cannonballForcesCalculator = CannonballForcesCalculator(boxWalls, boxWidth, boxHeight)
        val integrator = BeemanIntegrator(cannonballForcesCalculator, timeDelta, particles, boxWalls)
        val cannonballFileGenerator = CannonballFileGenerator("cannonball-" + String.format("%.6f", timeDelta))
        val cutCondition = TimeCutCondition(cutoffTime)
        val simulator =
            TimeStepSimulator(timeDelta, saveTimeDelta, cutCondition, integrator, cannonballFileGenerator, particles)
        simulator.simulate(true)
    }

    private fun createCannonBall(): Particle {
        val velocityMagnitude = 850.0
        val angle = Math.toRadians(100.0)
        val velocity = Vector(0.0, velocityMagnitude * sin(angle), -velocityMagnitude * sin(angle))
        val position = Vector(boxWidth / 2, (-boxWidth / 3), 2 * boxHeight)
//        val angle = Math.PI / 2
//        val velocity = Vector(0.0, 0.0, -velocityMagnitude * sin(angle))
//        val position = Vector(boxWidth / 2.0, boxWidth / 2.0, 2 * boxHeight)
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
            Wall(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), wKn, wKt, wGammaN, wGammaT, "LEFT"),

            // Right wall: se sitúa en el punto (boxWidth, 0, 0) y su normal apunta hacia la izquierda (-1,0,0).
            Wall(Vector(boxWidth, 0.0, 0.0), Vector(-1.0, 0.0, 0.0), wKn, wKt, wGammaN, wGammaT, "RIGHT"),

            // Front wall: se sitúa en el punto (0,0,0) y su normal apunta hacia atrás (0,1,0).
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0), wKn, wKt, wGammaN, wGammaT, "FRONT"),

            // Back wall: se sitúa en el punto (0, boxWidth, 0) y su normal apunta hacia adelante (0,-1,0).
            Wall(Vector(0.0, boxWidth, 0.0), Vector(0.0, -1.0, 0.0), wKn, wKt, wGammaN, wGammaT, "BACK"),

            // Bottom wall: se sitúa en el punto (0,0,0) y su normal apunta hacia arriba (0,0,1). Esta es la base de la caja.
            Wall(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 1.0), wKn, wKt, wGammaN, wGammaT, "BOTTOM"),
        )
    }
}