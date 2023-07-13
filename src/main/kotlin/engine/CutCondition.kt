package engine

import engine.model.Particle

interface CutCondition {
    fun isFinished(particles: Set<Particle>, time: Double): Boolean
}