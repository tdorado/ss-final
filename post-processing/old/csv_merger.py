import pandas as pd
from file_to_csv import file_to_csv
from tqdm import tqdm

for variation_name in tqdm(['nParticles_3000', 'nParticles_4500']):
    REPETITIONS = 5
    # Inicializar un DataFrame vacío
    df_total = pd.DataFrame()

    # Iterar sobre los nombres de archivo
    for REPETITION in tqdm(range(REPETITIONS), leave=False):
        # Usar la función para obtener un DataFrame de este archivo
        filename = f"{variation_name}_rep_{REPETITION}.xyz" 

        df = file_to_csv(filename)
        
        # Añadir este DataFrame al DataFrame total
        df_total = pd.concat([df_total, df])

    df_total['kineticEnergy'] = 0.5 * df_total['mass'] * (df_total['xVelocity']**2 + df_total['yVelocity']**2 + df_total['zVelocity']**2)

    df_total.to_csv(variation_name + '.csv')
