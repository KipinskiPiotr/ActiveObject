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
                f.write("%d, %f, %f, %d, %d, %d\n" % (t, m, v[0], v[1], v[2], v[3] + v[4]))
            for v in async_data:
                f.write("%d, %f, %f, %d, %d, %d\n" % (t+1, m, v[0], v[1], v[2], v[3] + v[4]))
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

    async_avg_time = 0
    prod_work_avg = 0
    cons_work_avg = 0
    params['type'] = 'asynchronously'
    for i in range(0, no_tests-1):
        results = run_java(params)
        async_avg_time += results[0]
        prod_work_avg += results[3]
        cons_work_avg += results[4]
    async_avg = run_java(params)
    async_avg[0] = (async_avg[0]+async_avg_time)/no_tests/1000  # seconds
    async_avg[3] = (async_avg[3]+async_avg_time)/no_tests
    async_avg[4] = (async_avg[4]+async_avg_time)/no_tests
    async_data.append(async_avg)

    sync_avg_time = 0
    params['type'] = 'synchronously'
    for i in range(0, no_tests-1):
        sync_avg_time += run_java(params)[0]
    sync_avg = run_java(params)
    sync_avg[0] = (sync_avg[0]+sync_avg_time)/no_tests/1000  # seconds
    sync_data.append(sync_avg)


def save_tests(params, file_name):
    sync_data = []
    async_data = []
    for prod in range(100, 200, 100):
        run_tests(params, sync_data, async_data, prod)

    append_to_csv(sync_data, async_data, file_name, params)


def plot_data3d(file_name):
    data = np.recfromcsv(file_name, delimiter=',', filling_values=np.nan, case_sensitive=True, deletechars='', replace_space=' ')
    print(data)
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    colors = [('red' if i % 2 == 0 else 'blue') for i in data['threads']]
    ax.scatter(data['finishTime'], data['productionsCounter'], data['threads'], c=colors)
    ax.set_xlabel('Time')
    ax.set_ylabel('Productions')
    ax.set_zlabel('Threads')
    blue_patch = mpatches.Patch(color='blue', label='Active Object')
    red_patch = mpatches.Patch(color='red', label='Synchroniczny')
    plt.legend(handles=[blue_patch, red_patch])
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


gather_data(starting_params, 'data.csv')
plot_data3d('data.csv')
print("Done!")
