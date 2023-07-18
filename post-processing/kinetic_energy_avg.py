import numpy as np
import matplotlib.pyplot as plt


def get_kinetic_energy_avg(data, time_interval):
    # Filtramos los datos para quedarnos sólo con aquellos cuyo tiempo modulo time_interval es 0
    data = data[np.isclose(data['time'] % time_interval, 0)].copy()

    # Calculamos la velocidad total
    v_total = np.sqrt(data['xVelocity'] ** 2 + data['yVelocity'] ** 2 + data['zVelocity'] ** 2)
    # Calculamos la energía cinética y la añadimos como una nueva columna
    data['kinetic_energy'] = 0.5 * data['mass'] * v_total ** 2

    # Agrupamos los datos por 'time' y 'repetition', y calculamos la sumatoria de la energía cinética por repetición
    data_grouped = data.groupby(['time', 'repetition'])['kinetic_energy'].sum().reset_index()

    # Agrupamos los datos por 'time' solamente, y calculamos la media y la desviación estándar de la energía cinética total
    kinetic_energy_stats = data_grouped.groupby('time')['kinetic_energy'].agg(['mean', 'std'])

    return kinetic_energy_stats


def plot_kinetic_energy_avg(kinetic_energy_stats, title):
    fig, ax = plt.subplots()

    # Graficamos la media de la energía cinética y las barras de error
    ax.errorbar(kinetic_energy_stats.index, kinetic_energy_stats['mean'], yerr=kinetic_energy_stats['std'], fmt='o-', capsize=5)

    ax.set_xlabel('Tiempo [s]', fontsize=16)
    ax.set_ylabel('Energía cinética [J]', fontsize=16)
    ax.set_title(title, fontsize=20)
    plt.yscale('log')
    plt.tight_layout()
    plt.show()


def plot_all_kinetic_energies(kinetic_energy_stats_dict):
    fig, ax = plt.subplots()

    # Graficamos cada conjunto de datos en el diccionario
    for name, kinetic_energy_stats in kinetic_energy_stats_dict.items():
        # Graficamos la media de la energía cinética
        ax.plot(kinetic_energy_stats.index, kinetic_energy_stats['mean'], label=name)

    ax.set_xlabel('Tiempo [s]', fontsize=16)
    ax.set_ylabel('Energía cinética [J]', fontsize=16)
    ax.set_title('Comparación de diferentes gamma', fontsize=20)
    ax.legend()
    plt.yscale('log')
    plt.tight_layout()
    plt.show()