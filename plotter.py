import numpy as np
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.patches as mpatches
import matplotlib.pyplot as plt


def plot_data3d(file_name, x, y, z, x_label, y_label, z_label, elev=30, azim=-60, animate=True):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='', replace_space=' ')
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    colors = [('red' if i % 2 == 0 else 'blue') for i in data['threads']]
    ax.scatter(data[x], data[y], data[z], c=colors)
    ax.set_xlabel(x_label)
    ax.set_ylabel(y_label)
    ax.set_zlabel(z_label)
    blue_patch = mpatches.Patch(color='blue', label='Active Object')
    red_patch = mpatches.Patch(color='red', label='Synchroniczny')
    plt.legend(handles=[blue_patch, red_patch])
    ax.view_init(elev=elev, azim=azim)
    if animate:
        counter = 1
        for i in range(-1, -89, -1):
            ax.view_init(elev=elev, azim=i)
            fig.savefig("animations/anim2-%d.png" % counter)
            counter += 1
    plt.show()


def plot_data2d(file_name, x, y, x_label, y_label):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='', replace_space=' ')
    fig = plt.figure()
    ax = fig.add_subplot(111)
    colors = [('red' if i % 2 == 0 else 'blue') for i in data['threads']]
    ax.scatter(data[x], data[y], c=colors)
    ax.set_xlabel(x_label)
    ax.set_ylabel(y_label)
    blue_patch = mpatches.Patch(color='blue', label='Active Object')
    red_patch = mpatches.Patch(color='red', label='Synchroniczny')
    plt.legend(handles=[blue_patch, red_patch])
    plt.show()
