package engine

import engine.model.Particle

interface CutCondition {
    fun isFinished(particles: List<Particle>, time: Double): Boolean
}