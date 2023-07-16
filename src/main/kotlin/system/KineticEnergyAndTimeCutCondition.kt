package system

import engine.TimeCutCondition
import engine.model.Particle
import mu.KotlinLogging
import kotlin.math.abs

class KineticEnergyAndTimeCutCondition(
    private val energyThreshold: Double,
    timeToCut: Double,
    private val shouldLog: Boolean = false,
) : TimeCutCondition(timeToCut) {
    private val logger = KotlinLogging.logger {}
    private val lastTenKineticEnergies = mutableListOf<Double>()
    private val listSize = 100
    private val minTime = 0.4

    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        if (super.isFinished(particles, time)) {
            return true
        }
        val kineticEnergy = particles.map { it.getKineticEnergy() }.reduce { acc, kineticEnergy -> acc + kineticEnergy }

        if (lastTenKineticEnergies.size < listSize) {
            lastTenKineticEnergies.add(kineticEnergy)
        } else if (lastTenKineticEnergies.size == listSize) {
            if (shouldStopByVariationOfLastKs(kineticEnergy)) {
                if (shouldLog) {
                    logger.info("Stopped by variation of last kinetic energies")
                }
                return time > minTime
            }
            lastTenKineticEnergies.removeFirst()
            lastTenKineticEnergies.add(kineticEnergy)
        }

        return time > minTime && kineticEnergy <= energyThreshold
    }

    private fun shouldStopByVariationOfLastKs(actualK: Double): Boolean {
        val average = lastTenKineticEnergies.sum() / listSize
        if (shouldLog) {
            logger.info("Average energy: $average")
        }
        return abs(average - actualK) < energyThreshold
    }
}