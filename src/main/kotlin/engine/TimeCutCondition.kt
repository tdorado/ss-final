package engine

import engine.model.Particle

class TimeCutCondition(var timeToCut: Double) : CutCondition {
    override fun isFinished(particles: Set<Particle>, time: Double): Boolean {
        return time >= timeToCut
    }
}