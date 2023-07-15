package engine

import engine.integrators.Integrator
import engine.model.Particle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
    private var particles: Set<Particle>,
) {
    private var time: Double
    private var timeToSave: Double

    private val logger: Logger = LoggerFactory.getLogger(CannonballParticleGenerator::class.java)

    init {
        timeToSave = saveTimeDelta
        time = 0.0
    }

    fun waitForParticlesToStabilize(): Set<Particle> {
        fileGenerator.addToFile(particles, time)
        val cutCondition = KineticEnergyCondition()
        var firstRun = true
        while ((firstRun || !cutCondition.isFinished(particles, 1E-3) || time < 0.5) && time < 0.55) {
            particles = runBlocking {
                particles.map { particle ->
                    async(Dispatchers.Default) {
                        integrator.applyIntegrator(timeDelta, particle, particles - particle)
                    }
                }.awaitAll().toSet()
            }
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] New iteration for simulation with time $time")

            firstRun = false
            time += timeDelta
            if (time >= timeToSave) {
                fileGenerator.addToFile(particles, timeToSave)
                timeToSave += saveTimeDelta
            }
        }
        return particles
    }

    fun simulate(closeFile: Boolean) {
        fileGenerator.addToFile(particles, time)
        while (!cutCondition.isFinished(particles, time)) {
            val currentDateTime = LocalDateTime.now()
            val formattedDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            logger.info("[$formattedDateTime] New iteration for simulation with time $time")

            val newParticles = runBlocking {
                particles.map { particle ->
                    async(Dispatchers.Default) {
                        integrator.applyIntegrator(timeDelta, particle, particles - particle)
                    }
                }.awaitAll().toSet()
            }

            time += timeDelta
            if (time >= timeToSave) {
                fileGenerator.addToFile(particles, timeToSave)
                timeToSave += saveTimeDelta
            }
            particles = newParticles
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