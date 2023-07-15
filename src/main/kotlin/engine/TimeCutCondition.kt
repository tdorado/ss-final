package engine

import engine.model.Particle

open class TimeCutCondition(private val timeToCut: Double) : CutCondition {

    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        return time >= timeToCut
    }
}