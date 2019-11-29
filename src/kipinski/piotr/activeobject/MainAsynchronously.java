package kipinski.piotr.activeobject;

import kipinski.piotr.common.Configuration;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class MainAsynchronously {

    public static void main(String[] args) throws InterruptedException {
        BufferProxy buffer = new BufferProxy(Configuration.BUFFER_SIZE, Configuration.BUFFER_WORK_TIME_MULTIPLIER);
        int timeQuantum = Configuration.ASYNC_TIME_QUANTUM;

        List<Thread> threads = new ArrayList<>();
        IntStream.range(0, Configuration.PRODUCERS_NUM)
                .forEach(e -> threads.add(new Producer(buffer, timeQuantum)));
        IntStream.range(0, Configuration.CONSUMERS_NUM)
                .forEach(e -> threads.add(new Consumer(buffer, timeQuantum)));
        Collections.shuffle(threads, new Random(Configuration.RANDOM_SEED));

        System.out.println("Starting asynchronously test");
        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        if (Configuration.INFINITE_MODE) {
            sleep(Configuration.TIMEOUT);
            int producersAdditionalWorkDone = 0;
            int productionsCounter = 0;
            int consumersAdditionalWorkDone = 0;
            int consumptionsCounter = 0;
            for (Thread thread : threads) {
                if (thread instanceof Producer) {
                    producersAdditionalWorkDone += ((Producer) thread).getAdditionalWorkDone();
                    productionsCounter += ((Producer) thread).getCounter();
                } else {
                    consumersAdditionalWorkDone += ((Consumer) thread).getAdditionalWorkDone();
                    consumptionsCounter += ((Consumer) thread).getCounter();
                }
            }
            System.out.println("Producers additional work done: " + producersAdditionalWorkDone + " ms");
            System.out.println("Productions counter: " + productionsCounter);
            System.out.println("Consumers additional work done: " + consumersAdditionalWorkDone + " ms");
            System.out.println("Consumptions counter: " + consumptionsCounter);

            for (Thread thread : threads) {
                thread.stop();
            }
        } else {
            for (Thread thread : threads) {
                thread.join();
            }
            long stopTime = System.currentTimeMillis();
            System.out.println("Program finished in " + (stopTime - startTime));

            int producersAdditionalWorkDone = 0;
            int productionsCounter = 0;
            int consumersAdditionalWorkDone = 0;
            int consumptionsCounter = 0;
            for (Thread thread : threads) {
                if (thread instanceof Producer) {
                    producersAdditionalWorkDone += ((Producer) thread).getAdditionalWorkDone();
                    productionsCounter += ((Producer) thread).getCounter();
                } else {
                    consumersAdditionalWorkDone += ((Consumer) thread).getAdditionalWorkDone();
                    consumptionsCounter += ((Consumer) thread).getCounter();
                }
            }
            System.out.println("Producers additional work done: " + producersAdditionalWorkDone + " ms");
            System.out.println("Productions counter: " + productionsCounter);
            System.out.println("Consumers additional work done: " + consumersAdditionalWorkDone + " ms");
            System.out.println("Consumptions counter: " + consumptionsCounter);
        }
    }
}
