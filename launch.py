import json
import sys
from subprocess import Popen, PIPE
import numpy as np
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.patches as mpatches
import matplotlib.pyplot as plt


JAR_PATH = "target/active_object-1.0-SNAPSHOT-jar-with-dependencies.jar"
starting_params = {'timedMode': False,
                   'testTime': 60000,
                   'bufferWorkTimeMultiplier': 0.01,
                   'productionsPerProducer': 500,
                   'consumptionsPerConsumer': 500,
                   'bufferSize': 1000,
                   'producersNum': 1,
                   'consumersNum': 1,
                   'asyncTimeQuantum': 10,
                   'jsonOutput': True,
                   'type': 'asynchronously'}


def append_to_csv(sync_data, async_data, file_name, params):
    t = params['producersNum'] + params['consumersNum']
    m = params['bufferWorkTimeMultiplier']
    try:
        with open(file_name, 'a') as f:
            for v in sync_data:
                f.write("%d, %f, %f, %d, %d, %f\n" % (t, m, v[0], v[1], v[2], v[3] + v[4]))
            for v in async_data:
                f.write("%d, %f, %f, %d, %d, %f\n" % (t+1, m, v[0], v[1], v[2], v[3] + v[4]))
    except IOError:
        print("I/O error")


def run_java(params):
    process = Popen(["java", "-jar", JAR_PATH, json.dumps(params)], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()

    if exit_code != 0:
        print("Error occured:")
        print(output)
        print(err)
        sys.exit(1)
    else:
        pass

    results = json.loads(output)
    print(results)
    return list(results.values())


def run_tests(params, sync_data, async_data, prod, no_tests=3):
    params['productionsPerProducer'] = prod
    params['consumptionsPerConsumer'] = prod

    async_time_sum = 0
    prod_work_sum = 0
    cons_work_sum = 0
    params['type'] = 'asynchronously'
    for i in range(0, no_tests):
        results = run_java(params)
        async_time_sum += results[0]
        prod_work_sum += results[3]
        cons_work_sum += results[4]
        if i == no_tests - 1:
            results[0] = async_time_sum/no_tests/1000  # seconds
            results[3] = prod_work_sum/no_tests/1000
            results[4] = cons_work_sum/no_tests/1000
            async_data.append(results)

    sync_time_sum = 0
    params['type'] = 'synchronously'
    for i in range(0, no_tests):
        results = run_java(params)
        sync_time_sum += results[0]
        if i == no_tests - 1:
            results[0] = sync_time_sum/no_tests/1000  # seconds
            sync_data.append(results)


def save_tests(params, file_name):
    sync_data = []
    async_data = []
    for prod in range(100, 501, 100):
        run_tests(params, sync_data, async_data, prod)

    append_to_csv(sync_data, async_data, file_name, params)


def plot_data3d(file_name, x, y, z, x_label, y_label, z_label, elev=30, azim=-60, animate=False):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='', replace_space=' ')
    print(data)
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
        for i in range(-1, -89, -2):
            ax.view_init(elev=elev, azim=i)
            fig.savefig("animations/3d-%d.png" % counter)
            counter += 1
    plt.show()


def gather_data(params, file_name):
    with open('data.csv', 'w') as f:
        f.write('threads, bufferWorkTimeMultiplier,'
                ' finishTime, productionsCounter, consumptionsCounter,'
                ' additionalWorkDone\n')

    for i in [1, 2, 3, 4]:
        params['producersNum'] = i
        params['consumersNum'] = i
        save_tests(params, file_name)


#gather_data(starting_params, 'data.csv')
plot_data3d('data.csv', 'finishTime', 'productionsCounter', 'threads',
            x_label='Time (s)', y_label='Productions', z_label='Threads')
print("Done!")
