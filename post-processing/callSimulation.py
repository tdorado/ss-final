import subprocess

# Definir las variables
nParticles = None
outputFileName = None
timeDelta = None
repetitions = None
cutTime = None
bulletMass = None
bulletDiameter = None
bulletInitialVelocity = None
bulletInitialVelocityAngle = None
lowDiam = None
upperDiam = None
particleMass = None
boxSideLength = None
boxHeight = None
frictionCoefficient = None

# Inicializar la lista de argumentos
args = ["kotlin", "Main.kt"]

# Agregar cada argumento si no es None
if nParticles is not None:
    args.extend(["-n", str(nParticles)])
if outputFileName is not None:
    args.extend(["-o", outputFileName])
if timeDelta is not None:
    args.extend(["-dt", str(timeDelta)])
if repetitions is not None:
    args.extend(["-r", str(repetitions)])
if cutTime is not None:
    args.extend(["-ct", str(cutTime)])
if bulletMass is not None:
    args.extend(["-bm", str(bulletMass)])
if bulletDiameter is not None:
    args.extend(["-bd", str(bulletDiameter)])
if bulletInitialVelocity is not None:
    args.extend(["-bv", str(bulletInitialVelocity)])
if bulletInitialVelocityAngle is not None:
    args.extend(["-bva", str(bulletInitialVelocityAngle)])
if lowDiam is not None:
    args.extend(["-pld", str(lowDiam)])
if upperDiam is not None:
    args.extend(["-pud", str(upperDiam)])
if particleMass is not None:
    args.extend(["-pm", str(particleMass)])
if boxSideLength is not None:
    args.extend(["-bxs", str(boxSideLength)])
if boxHeight is not None:
    args.extend(["-bxh", str(boxHeight)])
if frictionCoefficient is not None:
    args.extend(["-fr", str(frictionCoefficient)])

# Ejecutar el comando
subprocess.run(args)
