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


def save_to_csv(sync_data, async_data, file_name, params):
    t = params['producersNum'] + params['consumersNum']
    m = params['bufferWorkTimeMultiplier']
    try:
        with open(file_name, 'w') as f:
            f.write('threads, bufferWorkTimeMultiplier,'
                    ' finishTime, productionsCounter, consumptionsCounter,'
                    ' additionalWorkDone\n')
            for v in sync_data:
                f.write("%d, %f, %d, %d, %d, %d\n" % (t, m, v[0], v[1], v[2], v[3] + v[4]))
            for v in async_data:
                f.write("%d, %f, %d, %d, %d, %d\n" % (t+1, m, v[0], v[1], v[2], v[3] + v[4]))
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


def run_tests(params, sync_data, async_data, prod, no_tests=5):
    params['productionsPerProducer'] = prod
    params['consumptionsPerConsumer'] = prod

    params['type'] = 'asynchronously'
    for i in range(0, no_tests):
        async_data.append(run_java(params))

    params['type'] = 'synchronously'
    for i in range(0, no_tests):
        sync_data.append(run_java(params))


def save_tests(params, file_name):
    sync_data = []
    async_data = []
    for prod in range(100, 200, 100):
        run_tests(params, sync_data, async_data, prod)

    save_to_csv(sync_data, async_data, file_name, params)


def plot_data3d(file_name):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='', replace_space=' ')
    print(data)
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    colors = [('blue' if i % 2 else 'red') for i in data['threads']]
    ax.scatter(data['finishTime'], data['productionsCounter'], data['threads'], c=colors)
    ax.set_xlabel('Time')
    ax.set_ylabel('Productions')
    ax.set_zlabel('Threads')
    red_patch = mpatches.Patch(color='red', label='Synchroniczny')
    blue_patch = mpatches.Patch(color='blue', label='Active Object')
    plt.legend(handles=[red_patch, blue_patch])
    plt.show()
    print(data['threads'])
    print(type(data))


#save_tests(starting_params, 'data1.csv')
plot_data3d('data1.csv')
print("Done!")
