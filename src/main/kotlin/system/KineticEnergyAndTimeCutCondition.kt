package system

import engine.TimeCutCondition
import engine.model.Particle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs

class KineticEnergyAndTimeCutCondition(
    private val energyThreshold: Double,
    timeToCut: Double
) : TimeCutCondition(timeToCut) {
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
                return time > minTime
            }
            lastTenKineticEnergies.removeFirst()
            lastTenKineticEnergies.add(kineticEnergy)
        }

        return time > minTime && kineticEnergy <= energyThreshold
    }

    private fun shouldStopByVariationOfLastKs(actualK: Double): Boolean {
        val average = lastTenKineticEnergies.sum() / listSize
        return abs(average - actualK) < energyThreshold
    }
}