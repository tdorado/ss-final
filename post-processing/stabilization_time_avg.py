import numpy as np
import matplotlib.pyplot as plt


def plot_avg_stabilization_times(times_dict, x_label, title):
    # Calculamos la media y la desviación estándar de los tiempos de estabilización para cada gamma
    avg_times = {legend: np.mean(times) for legend, times in times_dict.items()}
    std_times = {legend: np.std(times) for legend, times in times_dict.items()}

    fig, ax = plt.subplots()

    # Graficamos una barra para cada gamma
    for i, legend in enumerate(times_dict.keys()):
        ax.bar(i, avg_times[legend], yerr=std_times[legend], capsize=5, label=legend)

    ax.set_xticks(range(len(times_dict)))  # Colocamos las marcas del eje x
    ax.set_xticklabels(times_dict.keys())  # Etiquetas del eje x con los valores de gamma
    ax.set_xlabel(x_label, fontsize=16)
    ax.set_ylabel('Tiempo de estabilización promedio [s]', fontsize=16)
    ax.set_title(title, fontsize=20)
    ax.legend()  # Mostramos la leyenda
    plt.tight_layout()
    plt.show()