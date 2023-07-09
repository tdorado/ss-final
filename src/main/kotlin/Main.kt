import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import system.CannonballSystem
import system.particle_generators.EfficientParticleGenerator

@Command(name = "Main", mixinStandardHelpOptions = true, version = ["1.0"])
class Main : Runnable {
    @Option(names = ["-n"], description = ["Number of particles"], required = false)
    var nParticles: Int = 1000

    @Option(names = ["-pGen"], description = ["True to run only generating particles"], required = false)
    var pGen: Boolean = false

    @Option(names = ["-pFile"], description = ["File path to load particles from it"], required = false)
    var particleFile: String = ""

    @Option(names = ["-o"], description = ["Output file name"], required = false)
    var outputFileName: String = "output.xyz"

    @Option(names = ["-dt"], description = ["Time delta in seconds"], required = false)
    var timeDelta: Double = 0.05

    @Option(names = ["-r"], description = ["Number of repetitions"], required = false)
    var repetitions: Int = 1

    @Option(names = ["-ct"], description = ["Cut time after equilibrium in seconds"], required = false)
    var cutTime: Int = 5

    @Option(names = ["-bm"], description = ["Bullet mass in kg"], required = false)
    var bulletMass: Double = 17.6

    @Option(names = ["-bd"], description = ["Bullet diameter mm"], required = false)
    var bulletDiameter: Double = 175.0

    @Option(names = ["-bv"], description = ["Bullet velocity in m/s"], required = false)
    var bulletInitialVelocity: Double = 450.0

    @Option(names = ["-bva"], description = ["Bullet velocity angle in radians"], required = false)
    var bulletInitialVelocityAngle: Double = Math.PI / 2 // 90 degrees

    @Option(names = ["-pld"], description = ["Lower bound of particle's diameter"], required = false)
    var lowDiam: Double = 0.02

    @Option(names = ["-pud"], description = ["Upper bound of particle's diameter"], required = false)
    var upperDiam: Double = 0.05

    @Option(names = ["-pm"], description = ["Particle mass in kg"], required = false)
    var particleMass: Double = 0.01

    @Option(names = ["-bxs"], description = ["Box side length in meters"], required = false)
    var boxSideLength: Double = 1.0

    @Option(names = ["-bxh"], description = ["Box height in meters"], required = false)
    var boxHeight: Double = 1.0

    @Option(names = ["-fr"], description = ["Friction coefficient"], required = false)
    var frictionCoefficient: Double = 0.4

    override fun run() {
        println("n_particles: $nParticles")
        println("outputFileName: $outputFileName")
        println("timeDelta: $timeDelta")
        println("repetitions: $repetitions")
        println("cutTime: $cutTime")
        println("bulletMass: $bulletMass")
        println("bulletDiameter: $bulletDiameter")
        println("bulletInitialVelocity: $bulletInitialVelocity")
        println("bulletInitialVelocityAngle: $bulletInitialVelocityAngle")
        println("lowDiam: $lowDiam")
        println("upperDiam: $upperDiam")
        println("particleMass: $particleMass")
        println("boxSideLength: $boxSideLength")
        println("boxHeight: $boxHeight")
        println("frictionCoefficient: $frictionCoefficient")
        val cannonballSystem = CannonballSystem()

        if (pGen) {

            val particleGenerator = EfficientParticleGenerator(
                CannonballSystem.particleMass,
                CannonballSystem.particlesMinRadius,
                CannonballSystem.particlesMaxRadius,
                CannonballSystem.boxSize,
                nParticles,
                cannonballSystem.createBoxWalls(),
                CannonballSystem.particlesDiameterGenerator,
                0.0,
                CannonballSystem.boxParticlesFrictionCoefficient
            )
            particleGenerator.generateParticles(true)
            particleGenerator.exportParticlesToFile("particles/particles_50k")
        } else {
            if (particleFile.isEmpty()) {
                cannonballSystem.run()
            } else {
                val particles = EfficientParticleGenerator.importParticlesFromFile(particleFile)
                cannonballSystem.run(particles)
            }
        }
    }


}

fun main(args: Array<String>) {
    val commandLine = CommandLine(Main())
    commandLine.execute(*args)
}