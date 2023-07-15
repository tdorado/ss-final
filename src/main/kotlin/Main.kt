import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import system.CannonballSystem

@Command(name = "Main", mixinStandardHelpOptions = true, version = ["1.0"])
class Main : Runnable {
    @Option(names = ["-n"], description = ["Number of particles"], required = false)
    var nParticles: Int = 2000

    @Option(names = ["-pGen"], description = ["True to run only generating particles"], required = false)
    var pGen: Boolean = false

    @Option(names = ["-pFile"], description = ["File path to load particles from it"], required = false)
    var particleFile: String = ""

    @Option(names = ["-o"], description = ["Output file name"], required = false)
    var outputFileName: String = "output.xyz"

    @Option(names = ["-dt"], description = ["Time delta in seconds"], required = false)
    var timeDelta: Double = 0.00005

    @Option(names = ["-r"], description = ["Number of repetitions"], required = false)
    var repetitions: Int = 1

    @Option(names = ["-ct"], description = ["Cut time after equilibrium in seconds"], required = false)
    var cutTime: Int = 5

    @Option(names = ["-bm"], description = ["Bullet mass in kg"], required = false)
    var bulletMass: Double = 17.5

    @Option(names = ["-bd"], description = ["Bullet diameter mm"], required = false)
    var bulletDiameter: Double = 175.0

    @Option(names = ["-bv"], description = ["Bullet velocity in m/s"], required = false)
    var bulletInitialVelocity: Double = 10.0

    @Option(names = ["-bva"], description = ["Bullet velocity angle in radians"], required = false)
    var bulletInitialVelocityAngle: Double = Math.toRadians(90.0) // 90 degrees

    @Option(names = ["-pld"], description = ["Lower bound of particle's diameter"], required = false)
    var lowDiam: Double = 0.015

    @Option(names = ["-pud"], description = ["Upper bound of particle's diameter"], required = false)
    var upperDiam: Double = 0.025

    @Option(names = ["-pm"], description = ["Particle mass in kg"], required = false)
    var particleMass: Double = 0.025

    @Option(names = ["-bxs"], description = ["Box side length in meters"], required = false)
    var boxSideLength: Double = 0.445

    @Option(names = ["-bxh"], description = ["Box height in meters"], required = false)
    var boxHeight: Double = 1.0

    @Option(names = ["-fr"], description = ["Friction coefficient"], required = false)
    var frictionCoefficient: Double = 0.4

    override fun run() {
        val gammas = arrayOf(10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0)
        for (gamma in gammas) {
            val cannonballSystem = CannonballSystem(
                timeDelta = timeDelta,
                boxHeight = boxHeight,
                boxWidth = boxSideLength,
                numberOfParticles = nParticles,
                minParticleDiameter = lowDiam,
                maxParticleDiameter = upperDiam,
                particleMass = particleMass,
                cannonballAngle = bulletInitialVelocityAngle,
                cannonballVelocity = bulletInitialVelocity,
                cannonballMass = bulletMass,
                cannonballRadius = bulletDiameter / 2 / 1000,
                pFile = particleFile,
                pGamma = gamma
            )

            cannonballSystem.run()
        }
    }


}

fun main(args: Array<String>) {
    val commandLine = CommandLine(Main())
    commandLine.execute(*args)
}