package engine.model

class Wall(
    private val point: Vector,
    private val normal: Vector
) {
    private val tangent: Vector = normal.crossProduct(Vector(0.0, 0.0, 1.0))

    fun getPoint(): Vector {
        return this.point
    }

    fun getNormal(): Vector {
        return this.normal
    }

    fun getTangent(): Vector {
        return this.tangent
    }
}