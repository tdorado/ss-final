package engine

import engine.integrators.Integrator

class TimeStepSimulator(
    private val timeDelta: Double,
    private val saveTimeDelta: Double,
    private val cutCondition: CutCondition,
    private val integrator: Integrator,
    private val fileGenerator: FileGenerator,
    private val particles: List<Particle>
) {
    private var time: Double
    private var timeToSave: Double

    init {
        timeToSave = saveTimeDelta
        time = 0.0
    }

    fun simulate(closeFile: Boolean) {
        fileGenerator.addToFile(particles, time)
        while (!cutCondition.isFinished(particles, time)) {
            for (particle in particles) {
                if (!particle.isFixed) {
                    integrator.applyIntegrator(timeDelta, particle, particles)
                }
            }
            time += timeDelta
            if (time >= timeToSave) {
                fileGenerator.addToFile(particles, time)
                timeToSave += saveTimeDelta
            }
        }
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