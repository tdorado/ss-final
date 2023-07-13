package engine

import engine.integrators.Integrator
import engine.model.Particle
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import system.particle_generators.CannonballParticleGenerator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.CompletionService
import java.util.concurrent.ExecutorCompletionService

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

    fun simulate(closeFile: Boolean) {
//        var particlesWithoutBullet = particles.filter { it.id != 0 }.toSet()
//        // Till particles stabilize
//        while (!cutCondition.isFinished(particlesWithoutBullet, time)) {
//            val newParticles = runBlocking {
//                particlesWithoutBullet.map { particle ->
//                    async(Dispatchers.Default) {
//                        integrator.applyIntegrator(timeDelta * 2, particle, particlesWithoutBullet - particle)
//                    }
//                }.awaitAll().toSet()
//            }
//            time += timeDelta
//
//            logger.info("Time for stabilization $time")
//
//            particlesWithoutBullet = newParticles
//        }

        time = 0.0
//        particles = particlesWithoutBullet + particles.filter { it.id == 0 }
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
                fileGenerator.addToFile(particles, time)
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