package engine

import engine.integrators.Integrator
import engine.model.Particle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging


class TimeStepSimulator(
    private val timeDelta: Double,
    private val saveTimeDelta: Double,
    private val cutCondition: CutCondition,
    private val integrator: Integrator,
    private val fileGenerator: FileGenerator,
    private var particles: Set<Particle>,
) {
    private val logger = KotlinLogging.logger {}
    private var time: Double
    private var timeToSave: Double

    init {
        timeToSave = saveTimeDelta
        time = 0.0
    }

    fun simulate(closeFile: Boolean, shouldLog: Boolean = false): Set<Particle> {
        fileGenerator.addToFile(particles, time)
        while (!cutCondition.isFinished(particles, time)) {
            if (shouldLog) {
                logger.info("New iteration for simulation with time $time")
            }

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
        if (shouldLog) {
            logger.info("Simulation finished")
        }
        if (closeFile) {
            fileGenerator.closeFile()
        }
        return particles
    }

    fun getTime(): Double {
        return time
    }

    fun setTime(time: Double) {
        this.time = time
        timeToSave = time + saveTimeDelta
    }
}