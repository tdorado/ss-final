package engine.model

data class Particle(
    val id: Int,
    var position: Vector,
    var velocity: Vector,
    val radius: Double,
    val mass: Double,
    val frictionCoefficient: Double,
    var pressure: Double,
    var isOnTheGround: Boolean = false,
    var collideWithWall: Boolean = false
) {
    fun overlapsWith(otherPosition: Vector, otherRadius: Double): Boolean {
        return position.distance(otherPosition) < (radius + otherRadius)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Particle

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}