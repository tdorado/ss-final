package engine.model

class Particle(
    var position: Vector,
    var velocity: Vector,
    val radius: Double,
    val mass: Double,
    val isFixed: Boolean = true // FIXME hardcode
) {
    private var pressure: Double = 0.0

    fun getPosition(): Vector {
        return this.position
    }

    fun setPosition(position: Vector) {
        this.position = position
    }

    fun getVelocity(): Vector {
        return this.velocity
    }

    fun setVelocity(velocity: Vector) {
        this.velocity = velocity
    }

    fun getRadius(): Double {
        return this.radius
    }

    fun getMass(): Double {
        return this.mass
    }

    fun getPressure(): Double {
        return this.pressure
    }

    fun setPressure(pressure: Double) {
        this.pressure = pressure
    }

    fun getDistance(other: Particle): Double {
        return this.position.subtract(other.getPosition()).length()
    }
}