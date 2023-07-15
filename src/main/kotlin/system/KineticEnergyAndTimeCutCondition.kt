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

    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        if (super.isFinished(particles, time)) {
            return true
        }
        val kineticEnergy = particles.map { it.getKineticEnergy() }.reduce { acc, kineticEnergy -> acc + kineticEnergy }
        logger.info("Current kinetic energy: $kineticEnergy")
        return kineticEnergy <= energyThreshold
    }
}