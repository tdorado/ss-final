package system

import engine.TimeCutCondition
import engine.model.Particle
import mu.KotlinLogging
import kotlin.math.abs

class KineticEnergyAndTimeCutCondition(
    private val energyThreshold: Double,
    timeToCut: Double,
    private val minimumTime: Double,
    private val averageSize: Int,
    private val boundsLimit: Double,
    private val shouldLog: Boolean = false,
) : TimeCutCondition(timeToCut) {
    private val logger = KotlinLogging.logger {}
    private val lastTenKineticEnergies = mutableListOf<Double>()

    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        if (super.isFinished(particles, time)) {
            return true
        }
        val outOfBounds = shouldStopByParticleOutOfBounds(particles)
        if (outOfBounds) {
            return true
        }
        val kineticEnergy = particles.map { it.getKineticEnergy() }.reduce { acc, kineticEnergy -> acc + kineticEnergy }
        if (shouldLog) {
            logger.info("Current kinetic energy: $kineticEnergy")
        }
        if (lastTenKineticEnergies.size < averageSize) {
            lastTenKineticEnergies.add(kineticEnergy)
        } else if (lastTenKineticEnergies.size == averageSize) {
            if (shouldStopByVariationOfLastKs(kineticEnergy)) {
                if (shouldLog) {
                    logger.info("Stopped by variation of last kinetic energies")
                }
                return time > minimumTime
            }
            lastTenKineticEnergies.removeFirst()
            lastTenKineticEnergies.add(kineticEnergy)
        }

        return time > minimumTime && kineticEnergy <= energyThreshold
    }

    private fun shouldStopByVariationOfLastKs(actualK: Double): Boolean {
        val average = lastTenKineticEnergies.sum() / averageSize
        if (shouldLog) {
            logger.info("Average kinetic energy: $average")
        }
        return abs(average - actualK) < energyThreshold
    }

    private fun shouldStopByParticleOutOfBounds(particles: Set<Particle>): Boolean {
        val outOfBounds = particles.any { particle ->
            abs(particle.position.x) > boundsLimit || abs(particle.position.y) > boundsLimit
        }
        if (outOfBounds && shouldLog){
            logger.info("Particle out of bounds.")
        }
        return outOfBounds
    }
}