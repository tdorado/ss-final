package engine

import engine.integrators.Integrator
import engine.model.Particle
import engine.model.Wall

class TimeStepSimulator(
    private val timeDelta: Double,
    private val saveTimeDelta: Double,
    private val cutCondition: CutCondition,
    private val integrator: Integrator,
    private val fileGenerator: FileGenerator,
    private val particles: List<Particle>,
    private val walls: List<Wall>
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
                integrator.applyIntegrator(timeDelta, particle, particles, walls)
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