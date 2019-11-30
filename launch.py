import json
import sys
from pprint import pprint
from subprocess import Popen, PIPE
import matplotlib
import matplotlib.pyplot as plt
import numpy as np
import csv

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


save_tests(starting_params, 'data1.csv')
print("Done!")
