import json
import sys
from pprint import pprint
from subprocess import Popen, PIPE

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

    print(json.dumps(json.loads(output)))

params = {}
params['timedMode'] = False
params['testTime'] = 60000
params['bufferWorkTimeMultiplier'] = 0.01
params['productionsPerProducer'] = 500
params['consumptionsPerConsumer'] = 500
params['bufferSize'] = 1000
params['producersNum'] = 1
params['consumersNum'] = 1
params['asyncTimeQuantum'] = 10
params['maxProductionSize'] = 500
params['maxConsumptionSize'] = 500
params['jsonOutput'] = True
params['type'] = 'asynchronously'

make_test(params)
params['type'] = 'synchronously'
make_test(params)
print("Done!")




