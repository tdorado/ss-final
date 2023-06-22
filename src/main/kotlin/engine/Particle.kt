package engine

class Particle(
    var id: Int,
    var position: Vector,
    var velocity: Vector,
    var mass: Double,
    var radius: Double,
    var isFixed: Boolean,
) {
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