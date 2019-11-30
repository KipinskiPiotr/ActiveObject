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


def average_tests(params, no_tests=3):
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


def productions_time_test(params):
    synchro_times = {}
    asynchro_times = {}
    for prods in range(100, 500, 100):
        params['productionsPerProducer'] = prods
        params['consumptionsPerConsumer'] = prods

        params['type'] = 'asynchronously'
        asynchro_times.update({average_tests(params)/1000: prods})  # we want seconds

        params['type'] = 'synchronously'
        synchro_times.update({average_tests(params)/1000: prods})  # we want seconds

    x, y = zip(*asynchro_times.items())
    plt.plot(x, y, label='asynchronous')

    x, y = zip(*synchro_times.items())
    plt.plot(x, y, label='synchronous')

    plt.xlim(xmin=0)
    plt.ylim(ymin=0)
    plt.xlabel("Time: (s)")
    plt.ylabel("Productions: (Consumptions)")
    plt.legend()
    plt.show()


productions_time_test(starting_params)
print("Done!")
