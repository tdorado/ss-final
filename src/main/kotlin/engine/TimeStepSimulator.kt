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

    private fun relaxParticles(particles: Set<Particle>) {
        for (particle in particles) {
            for (otherParticle in particles) {
                if (particle != otherParticle && particle.overlapsWith(otherParticle.position, otherParticle.radius)) {
                    val overlapSize = (particle.radius + otherParticle.radius) - (otherParticle.position - particle.position).magnitude
                    val normalVector = (otherParticle.position - particle.position).normalize()

                    // Corrige la superposición moviendo las partículas lejos entre sí
                    val positionCorrection = normalVector * overlapSize * 0.5

                    // Aplicar la corrección solo a lo largo del eje Z, ya que estamos considerando un lecho de partículas
                    positionCorrection.x = 0.0
                    positionCorrection.y = 0.0

                    particle.position += positionCorrection
                    otherParticle.position -= positionCorrection
                }
            }
        }
    }
    fun simulate(closeFile: Boolean) {
        time = 0.0
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