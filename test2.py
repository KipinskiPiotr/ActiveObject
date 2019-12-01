import json
import sys
from subprocess import Popen, PIPE
import numpy as np
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.patches as mpatches
import matplotlib.pyplot as plt

JAR_PATH = "target/active_object-1.0-SNAPSHOT-jar-with-dependencies.jar"
starting_params = {'timedMode': False,
                   'testTime': 30000,
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
            f.write("%d, %f, %f\n" % (
                t, m, sync_data[0]))
            f.write("%d, %f, %f\n" % (
                t + 1, m, async_data[0]))
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


def run_tests(params, prod=None, no_tests=1):
    async_result = []
    sync_result = []
    if prod is not None:
        params['productionsPerProducer'] = prod
        params['consumptionsPerConsumer'] = prod

    params['type'] = 'asynchronously'

    async_time_sum = 0
    prod_work_sum = 0
    cons_work_sum = 0
    for i in range(0, no_tests):
        results = run_java(params)
        async_time_sum += results[0]
        prod_work_sum += results[3]
        cons_work_sum += results[4]
        if i == no_tests - 1:
            results[0] = async_time_sum / no_tests / 1000  # seconds
            results[1] = prod_work_sum / no_tests / 1000
            results[2] = cons_work_sum / no_tests / 1000
            async_result = results

    params['type'] = 'synchronously'

    sync_time_sum = 0
    for i in range(0, no_tests):
        results = run_java(params)
        sync_time_sum += results[0]
        if i == no_tests - 1:
            results[0] = sync_time_sum / no_tests / 1000  # seconds
            sync_result = results

    return sync_result, async_result


def save_tests(params, file_name):
    iterations = 20
    for threads in [1, 2, 4, 8]:
        for iteration in range(0, iterations, 1):
            params['producersNum'] = threads
            params['consumersNum'] = threads

            productions_per_producer = 2000 + (500 - 2000) * (iteration / iterations)
            all_productions_num = productions_per_producer
            productions_per_producer /= threads
            productions_per_producer = round(productions_per_producer)

            params['productionsPerProducer'] = productions_per_producer
            params['consumptionsPerConsumer'] = productions_per_producer

            params['bufferWorkTimeMultiplier'] = 0.005 + (0.05 - 0.005) * (iteration / iterations)
            sync_data, async_data = run_tests(params)
            print(sync_data[0], " - ", async_data[0])
            sync_data[0] /= all_productions_num
            async_data[0] /= all_productions_num
            async_data[1] /= all_productions_num
            async_data[2] /= all_productions_num

            append_to_csv(sync_data, async_data, file_name, params)


def plot_data3d(file_name, x, y, z, x_label, y_label, z_label, elev=30, azim=-60, animate=True):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='',
                         replace_space=' ')
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
        for i in range(-1, -89, -1):
            ax.view_init(elev=elev, azim=i)
            fig.savefig("animations/anim2-%d.png" % counter)
            counter += 1
    plt.show()


def gather_data(params, file_name, append=False):
    if not append:
        with open(file_name, 'w') as f:
            f.write('threads, bufferWorkTimeMultiplier,'
                    ' finishTime, additionalWorkDoneProducers, additionalWorkDoneConsumers\n')

    save_tests(params, file_name)


gather_data(starting_params, 'data.csv', append=False)
plot_data3d('data.csv', 'threads', 'bufferWorkTimeMultiplier', 'finishTime',
            x_label='threads', y_label='bufferWorkTimeMultiplier', z_label='finishTime', animate=False)
# print("Done!")

#
# run_java({
#     'timedMode': False,
#     'bufferWorkTimeMultiplier': 0.005,
#     'productionsPerProducer': 2000,
#     'consumptionsPerConsumer': 2000,
#     'consumersNum': 1,
#     'producersNum': 1,
#     'type': 'synchronously',
#     'jsonOutput': True,
# })
#
# run_java({
#     'timedMode': False,
#     'bufferWorkTimeMultiplier': 0.005,
#     'productionsPerProducer': 2000,
#     'consumptionsPerConsumer': 2000,
#     'consumersNum': 1,
#     'producersNum': 1,
#     'type': 'asynchronously',
#     'jsonOutput': True,
# })
#
# run_java({
#     'timedMode': False,
#     'bufferWorkTimeMultiplier': 0.05,
#     'productionsPerProducer': 500,
#     'consumptionsPerConsumer': 500,
#     'consumersNum': 1,
#     'producersNum': 1,
#     'type': 'synchronously',
#     'jsonOutput': True,
# })
#
# run_java({
#     'timedMode': False,
#     'bufferWorkTimeMultiplier': 0.05,
#     'productionsPerProducer': 500,
#     'consumptionsPerConsumer': 500,
#     'consumersNum': 1,
#     'producersNum': 1,
#     'type': 'asynchronously',
#     'jsonOutput': True,
# })
