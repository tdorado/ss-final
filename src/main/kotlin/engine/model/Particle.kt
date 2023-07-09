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

    fun serialize(): String {
        return "$id,$position,$velocity,$radius,$mass,$frictionCoefficient,$pressure"
    }

    companion object {
        fun deserialize(line: String): Particle {
            val parts = line.split(",")
            return Particle(
                parts[0].toInt(),
                Vector.fromString(parts[1]),
                Vector.fromString(parts[2]),
                parts[3].toDouble(),
                parts[4].toDouble(),
                parts[5].toDouble(),
                parts[6].toDouble()
            )
        }
    }

    override fun hashCode(): Int {
        return id
    }
}