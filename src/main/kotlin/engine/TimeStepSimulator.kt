package engine

import engine.integrators.Integrator
import engine.model.Particle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.particle_generators.CannonballParticleGenerator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeStepSimulator(
    private val timeDelta: Double,
    private val saveTimeDelta: Double,
    private val cutCondition: CutCondition,
    private val integrator: Integrator,
    private val fileGenerator: FileGenerator,
    private val particles: Set<Particle>,
) {
    private var time: Double
    private var timeToSave: Double

    private val logger: Logger = LoggerFactory.getLogger(CannonballParticleGenerator::class.java)

    init {
        timeToSave = saveTimeDelta
        time = 0.0

    }

    fun simulate(closeFile: Boolean) {
        fileGenerator.addToFile(particles, time)
        while (!cutCondition.isFinished(particles, time)) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] New iteration for simulation with time $time")
            for (particle in particles) {
                integrator.applyIntegrator(timeDelta, particle, particles)
            }
            time += timeDelta
            if (time >= timeToSave) {
                fileGenerator.addToFile(particles, time)
                timeToSave += saveTimeDelta
            }
        }
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        logger.info("[$formattedDateTime] Simulation finished")
        if (closeFile) {
            fileGenerator.closeFile()
        }
    }

    fun getTime(): Double {
        return time
    }

    fun setTime(time: Double) {
        this.time = time
        timeToSave = time + saveTimeDelta
    }
}