import json
import sys
from pprint import pprint
from subprocess import Popen, PIPE
import matplotlib
import matplotlib.pyplot as plt
import numpy as np

JAR_PATH = "target/active_object-1.0-SNAPSHOT-jar-with-dependencies.jar"


def make_test(params):
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
    return results['finishTime']


def average_tests(params, no_tests=5):
    result = 0
    for i in range(no_tests):
        result += make_test(params)
    return result / no_tests


starting_params = {'timedMode': False,
                   'testTime': 60000,
                   'bufferWorkTimeMultiplier': 0.01,
                   'productionsPerProducer': 500,
                   'consumptionsPerConsumer': 500,
                   'bufferSize': 1000,
                   'producersNum': 1,
                   'consumersNum': 1,
                   'asyncTimeQuantum': 10,
                   'maxProductionSize': 500,
                   'maxConsumptionSize': 500,
                   'jsonOutput': True,
                   'type': 'asynchronously'}


def evaluate(params, sync_times, async_times, prod, no_tests=2):
    params['productionsPerProducer'] = prod
    params['consumptionsPerConsumer'] = prod

    params['type'] = 'asynchronously'
    async_times.update({average_tests(params, no_tests)/1000: prod})  # we want seconds

    params['type'] = 'synchronously'
    sync_times.update({average_tests(params, no_tests)/1000: prod})  # we want seconds


def productions_time_test(params):
    sync_times = {}
    async_times = {}
    for prod in range(50, 500, 50):
        evaluate(params, sync_times, async_times, prod)
    for prod in range(500, 1000, 100):
        evaluate(params, sync_times, async_times, prod)
    for prod in range(1000, 2001, 200):
        evaluate(params, sync_times, async_times, prod)

    x, y = zip(*async_times.items())
    plt.plot(x, y, label='Active Object')

    x, y = zip(*sync_times.items())
    plt.plot(x, y, label='Synchroniczny')

    plt.xlim(xmin=0)
    plt.ylim(ymin=0)
    plt.xlabel("Czas: (s)")
    plt.ylabel("Produkcje: (Konsumpcje)")
    plt.legend()
    plt.show()


productions_time_test(starting_params)
print("Done!")
