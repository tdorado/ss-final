import pandas as pd

# Nombre del archivo a leer
archivo = '../out/Stabilization_dtnP:2000_pMass:0.025_minDiameter:0.015_maxDiameter:0.025_angle:1.5707963267948966_pKt:2000000.0_pKn:1000000.0_pGamma:50.0_cutoffTime:1.0_TimeDelta:0.001_timeDelta:5.0E-5_wallGamma50.0_wallKn1000.0_parallel.xyz'
csv_salida = 'salida.csv'

# Abre el archivo
with open(archivo, 'r') as f:
    lineas = f.readlines()

# Variable para almacenar todos los datos
datos = []

# Itera sobre cada línea del archivo
i = 0
while i < len(lineas):
    try:
        # Intenta convertir la línea actual a un número
        n = int(lineas[i].strip())
        
        # Si se logra, entonces es una línea con la cantidad de líneas de datos que sigue.
        # Incrementamos el índice en 2 para saltar la línea del encabezado.
        i += 2
        
        # Itera sobre las próximas n líneas y añade los datos a la lista
        for j in range(i, i+n):
            # Separa los datos por espacios y los convierte a números
            fila = list(map(float, lineas[j].strip().split()))
            datos.append(fila)
            
        # Avanza al inicio del próximo bloque de datos
        i += n
        
    except ValueError:
        # Si la línea actual no puede ser convertida a un número, entonces es una línea de datos.
        # Simplemente la añadimos a la lista de datos.
        fila = list(map(float, lineas[i].strip().split()))
        datos.append(fila)
        i += 1

# Crea un DataFrame de pandas con los datos
df = pd.DataFrame(datos, columns=['id', 'xPosition', 'yPosition', 'zPosition', 
                                  'xVelocity', 'yVelocity', 'zVelocity', 
                                  'radius', 'mass', 'pressure', 'time'])

# Escribe el DataFrame a un archivo CSV
df.to_csv(csv_salida, index=False)