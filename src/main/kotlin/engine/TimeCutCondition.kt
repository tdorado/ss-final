package engine

class TimeCutCondition(var timeToCut: Double) : CutCondition {
    override fun isFinished(particles: List<Particle>, time: Double): Boolean {
        return time >= timeToCut
    }
}