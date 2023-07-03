package engine

interface CutCondition {
    fun isFinished(particles: List<Particle>, time: Double): Boolean
}