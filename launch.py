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
            f.write("%d, %f, %f, %d, %d, %f\n" % (t, m, sync_data[0], sync_data[1], sync_data[2], sync_data[3] + sync_data[4]))
            f.write("%d, %f, %f, %d, %d, %f\n" % (t+1, m, async_data[0], async_data[1], async_data[2], async_data[3] + async_data[4]))
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


def run_tests(params, prod=None, no_tests=3):
    async_result = []
    sync_result = []
    if prod is not None:
        params['productionsPerProducer'] = prod
        params['consumptionsPerConsumer'] = prod

    params['type'] = 'asynchronously'
    if params['timedMode']:
        prod_counter = 0
        cons_counter = 0
        prod_work_sum = 0
        cons_work_sum = 0
        for i in range(0, no_tests):
            results = run_java(params)
            prod_counter += results[0]
            cons_counter += results[1]
            prod_work_sum += results[2]
            cons_work_sum += results[3]
            if i == no_tests - 1:
                results[0] = prod_counter/no_tests
                results[1] = cons_counter/no_tests
                results[2] = prod_work_sum/no_tests/1000
                results[3] = cons_work_sum/no_tests/1000
                results.insert(0, params['testTime']/1000)
                async_result = results
    else:
        async_time_sum = 0
        prod_work_sum = 0
        cons_work_sum = 0
        for i in range(0, no_tests):
            results = run_java(params)
            async_time_sum += results[0]
            prod_work_sum += results[3]
            cons_work_sum += results[4]
            if i == no_tests - 1:
                results[0] = async_time_sum/no_tests/1000  # seconds
                results[3] = prod_work_sum/no_tests/1000
                results[4] = cons_work_sum/no_tests/1000
                async_result = results

    params['type'] = 'synchronously'
    if params['timedMode']:
        prod_counter = 0
        cons_counter = 0
        for i in range(0, no_tests):
            results = run_java(params)
            prod_counter += results[0]
            cons_counter += results[1]
            if i == no_tests - 1:
                results[0] = prod_counter/no_tests
                results[1] = cons_counter/no_tests
                results.insert(0, params['testTime']/1000)
                sync_result = results
    else:
        sync_time_sum = 0
        for i in range(0, no_tests):
            results = run_java(params)
            sync_time_sum += results[0]
            if i == no_tests - 1:
                results[0] = sync_time_sum/no_tests/1000  # seconds
                sync_result = results

    return sync_result, async_result


def save_tests(params, file_name):
    if params['timedMode']:
        for multiplier in range(1, 12, 10):
            for time in range(10000, 20001, 10000):
                params['bufferWorkTimeMultiplier'] = multiplier/100
                params['testTime'] = time
                sync_data, async_data = run_tests(params)
                append_to_csv(sync_data, async_data, file_name, params)
    else:
        #params['producersNum']
        for prod in range(100, 501, 100):
            sync_data, async_data = run_tests(params, prod=prod)
            append_to_csv(sync_data, async_data, file_name, params)


def plot_data3d(file_name, x, y, z, x_label, y_label, z_label, elev=30, azim=-60, animate=True):
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
        for i in range(-1, -89, -1):
            ax.view_init(elev=elev, azim=i)
            fig.savefig("animations/anim2-%d.png" % counter)
            counter += 1
    plt.show()


def gather_data(params, file_name, append=False):
    if not append:
        with open(file_name, 'w') as f:
            f.write('threads, bufferWorkTimeMultiplier,'
                    ' finishTime, productionsCounter, consumptionsCounter,'
                    ' additionalWorkDone\n')

    for i in [1, 2]:
        params['producersNum'] = i
        params['consumersNum'] = i
        params['timedMode'] = True
        save_tests(params, file_name)
        # params['timedMode'] = False
        # save_tests(params, file_name)


gather_data(starting_params, 'data.csv', append=False)
plot_data3d('data.csv', 'finishTime', 'productionsCounter', 'threads',
            x_label='Time (s)', y_label='Productions', z_label='Threads', animate=False)
print("Done!")
