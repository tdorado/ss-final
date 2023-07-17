import plotly.graph_objects as go
import numpy as np
import pandas as pd
from file_to_csv import file_to_csv
from tqdm import tqdm

def plot_kinetic_energy_in_time(df):
    # Calcula la energía cinética (KE = 1/2 * m * v^2)
    df['kineticEnergy'] = 0.5 * df['mass'] * (df['xVelocity']**2 + df['yVelocity']**2 + df['zVelocity']**2)

    # Redondea la columna de tiempo al más cercano 0.01 segundos
    df['time'] = df['time'].round(decimals=2)

    # Agrupa los datos por el tiempo y calcula la energía cinética promedio, el desvío estándar y el conteo en cada instante de tiempo
    grouped = df.groupby('time')['kineticEnergy'].agg(['mean', 'std']).reset_index()

    # Imprime la cantidad de puntos de datos, la media y el desvío estándar para cada intervalo de tiempo
    print(grouped)

    # Crea la gráfica
    fig = go.Figure()

    fig.add_trace(go.Scatter(
        x=grouped['time'],
        y=grouped['mean'],
        mode='lines+markers',
        error_y=dict(
            type='data', 
            array=grouped['std'], 
            visible=True
        )
    ))

    # Configura los ejes y el título
    fig.update_layout(title='Energía cinética en función del tiempo [J]', xaxis_title='Tiempo [segundos]', yaxis_title='Energía cinética', font=dict(size=20), yaxis_type="log")

    # Muestra la gráfica
    fig.show()


df = pd.read_csv("nParticles_3000.csv", engine='pyarrow')
plot_kinetic_energy_in_time(df)
