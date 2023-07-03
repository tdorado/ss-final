package engine.model

data class Particle(
    var position: Vector,
    var velocity: Vector,
    val radius: Double,
    val mass: Double,
    val isFixed: Boolean = true, // FIXME hardcode,
    var pressure: Double = 0.0
) {
    fun getDistance(other: Particle): Double {
        return this.position.subtract(other.position).length()
    }

    fun overlapsWith(otherPosition: Vector, otherRadius: Double): Boolean {
        return position.distance(otherPosition) < (radius + otherRadius)
    }
}