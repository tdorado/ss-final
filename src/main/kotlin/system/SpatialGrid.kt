package system

import engine.model.Particle
import engine.model.Vector


class SpatialGrid(private val size: Vector, val cellSize: Double) {
    private val cells: Array<Array<Array<MutableList<Particle>>>>

    init {
        val xCells = (size.x / cellSize).toInt()
        val yCells = (size.y / cellSize).toInt()
        val zCells = (size.z / cellSize).toInt()

        cells = Array(xCells) {
            Array(yCells) {
                Array(zCells) {
                    mutableListOf()
                }
            }
        }
    }

    fun addParticle(particle: Particle) {
        val cell = particleToCell(particle)
        cells[cell.x][cell.y][cell.z].add(particle)
    }

    fun findOverlappingParticle(position: Vector, radius: Double): Particle? {
        val cell = vectorToCell(position)
        // Check the cell and its neighboring cells for an overlapping particle
        for (i in -1..1) {
            for (j in -1..1) {
                for (k in -1..1) {
                    val x = cell.x + i
                    val y = cell.y + j
                    val z = cell.z + k
                    if (x in cells.indices && y in cells[x].indices && z in cells[x][y].indices) {
                        for (particle in cells[x][y][z]) {
                            if (particle.overlapsWith(position, radius)) {
                                return particle
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun particleToCell(particle: Particle): VectorInt {
        return vectorToCell(particle.position)
    }

    private fun vectorToCell(position: Vector): VectorInt {
        return VectorInt(
            (position.x / cellSize).toInt(),
            (position.y / cellSize).toInt(),
            (position.z / cellSize).toInt()
        )
    }
}

data class VectorInt(val x: Int, val y: Int, val z: Int)