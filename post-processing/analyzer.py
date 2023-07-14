import plotly.graph_objects as go
import numpy as np
import pandas as pd

df = pd.read_csv('salida.csv')

def plot_kinetic_energy_in_time(df):
    print('en kinetic energy', df)
        # Calcula la energía cinética (KE = 1/2 * m * v^2)
    df['kineticEnergy'] = 0.5 * df['mass'] * (df['xVelocity']**2 + df['yVelocity']**2 + df['zVelocity']**2)

    # Agrupa los datos por el tiempo y calcula la energía cinética total en cada instante de tiempo
    grouped = df.groupby('time')['kineticEnergy'].sum().reset_index()

    # Crea la gráfica
    fig = go.Figure()

    fig.add_trace(go.Scatter(x=grouped['time'], y=grouped['kineticEnergy'], mode='lines'))

    # Configura los ejes y el título
    fig.update_layout(title='Energía cinética en función del tiempo [J]', xaxis_title='Tiempo [segundos]', yaxis_title='Energía cinética', font=dict(size=20), yaxis_type="log")

    # Muestra la gráfica
    fig.show()

plot_kinetic_energy_in_time(df)