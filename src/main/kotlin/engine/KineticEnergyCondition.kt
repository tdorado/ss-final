package engine

import engine.model.Particle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KineticEnergyCondition : CutCondition {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)


    override fun isFinished(particles: Set<Particle>, threshold: Double): Boolean {
        val kineticEnergy = particles.map { it.getKineticEnergy() }.reduce { acc, kineticEnergy -> acc + kineticEnergy }
        logger.info("Current kinetic energy: $kineticEnergy")
        return kineticEnergy <= threshold
    }
}