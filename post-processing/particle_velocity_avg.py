import numpy as np
import matplotlib.pyplot as plt


def get_particle_velocity_avg(data, particle_id, time_interval):
    # Filtramos los datos para quedarnos sólo con aquellos cuyo tiempo modulo time_interval es 0
    data = data[np.isclose(data['time'] % time_interval, 0)].copy()
    # Filtramos los datos para quedarnos sólo con los de la partícula de interés
    data = data[data['id'] == particle_id].copy()

    # Calculamos la velocidad total
    v_total = np.sqrt(data['xVelocity'] ** 2 + data['yVelocity'] ** 2 + data['zVelocity'] ** 2)
    # Añadimos la velocidad total como una nueva columna
    data['total_velocity'] = v_total

    # Agrupamos los datos por 'time' y 'repetition', y calculamos la media y la desviación estándar de la velocidad total
    velocity_stats = data.groupby(['time', 'repetition'])['total_velocity'].sum().reset_index()

    # Agrupamos los datos por 'time' solamente, y calculamos la media y la desviación estándar de la velocidad total
    velocity_stats = velocity_stats.groupby('time')['total_velocity'].agg(['mean', 'std'])

    return velocity_stats


def plot_particle_velocity_avg(velocity_stats, title):
    fig, ax = plt.subplots()

    # Graficamos la media de la velocidad y las barras de error
    ax.errorbar(velocity_stats.index, velocity_stats['mean'], yerr=velocity_stats['std'], fmt='o-', capsize=5)

    ax.set_xlabel('Tiempo [s]', fontsize=16)
    ax.set_ylabel('Velocidad total [m/s]', fontsize=16)
    ax.set_title(title, fontsize=20)
    plt.tight_layout()
    plt.show()


def plot_all_velocities(velocity_stats_dict):
    fig, ax = plt.subplots()

    # Graficamos cada conjunto de datos en el diccionario
    for name, velocity_stats in velocity_stats_dict.items():
        # Graficamos la media de la velocidad total
        ax.plot(velocity_stats.index, velocity_stats['mean'], label=name)

    ax.set_xlabel('Tiempo [s]', fontsize=16)
    ax.set_ylabel('Velocidad total [m/s]', fontsize=16)
    ax.set_title('Comparación de diferentes', fontsize=20)
    ax.legend()
    plt.yscale('log')
    plt.tight_layout()
    plt.show()