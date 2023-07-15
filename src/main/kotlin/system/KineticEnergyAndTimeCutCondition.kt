package system

import engine.TimeCutCondition
import engine.model.Particle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KineticEnergyAndTimeCutCondition(
    private val energyThreshold: Double,
    timeToCut: Double
) : TimeCutCondition(timeToCut) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val lastTenKineticEnergies = mutableListOf<Double>()
    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        if (super.isFinished(particles, time)) {
            return true
        }
        val kineticEnergy = particles.map { it.getKineticEnergy() }.reduce { acc, kineticEnergy -> acc + kineticEnergy }
        logger.info("Current kinetic energy: $kineticEnergy")

        if (lastTenKineticEnergies.size < 10) {
            lastTenKineticEnergies.add(kineticEnergy)
        } else if (lastTenKineticEnergies.size == 10) {
            if (shouldStopByVariationOfLastKs()) {
                return time > 0.1
            }
            lastTenKineticEnergies.removeFirst()
            lastTenKineticEnergies.add(kineticEnergy)
        }

        return time > 0.1 && kineticEnergy <= energyThreshold
    }

    private fun shouldStopByVariationOfLastKs(): Boolean {
        return lastTenKineticEnergies.sum() / 10 < energyThreshold
    }
}