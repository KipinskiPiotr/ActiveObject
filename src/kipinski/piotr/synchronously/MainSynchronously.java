package kipinski.piotr.synchronously;

import kipinski.piotr.common.Configuration;

import java.util.Collections;

import static java.lang.Thread.sleep;

public class MainSynchronously {

    public static void main(String[] args) throws InterruptedException {
        SynchronizedBuffer buffer = new SynchronizedBuffer(Configuration.BUFFER_SIZE, 1);
        int timeQuantum = 10;

        Thread[] threads = new Thread[6];
        threads[0] = new Producer(buffer, timeQuantum);
        threads[1] = new Producer(buffer, timeQuantum);
        threads[2] = new Producer(buffer, timeQuantum);
        threads[3] = new Consumer(buffer, timeQuantum);
        threads[4] = new Consumer(buffer, timeQuantum);
        threads[5] = new Consumer(buffer, timeQuantum);

        System.out.println("Starting synchronously test");
        for (Thread thread : threads) {
            thread.start();
        }
        sleep(Configuration.TIMEOUT);
        int producersAdditionalWorkDone = 0;
        int productionsCounter = 0;
        int consumersAdditionalWorkDone = 0;
        int consumptionsCounter = 0;
        for (Thread thread : threads) {
            if(thread instanceof Producer){
                producersAdditionalWorkDone += ((Producer)thread).getAdditionalWorkDone();
                productionsCounter += ((Producer)thread).getCounter();
            }
            else{
                consumersAdditionalWorkDone += ((Consumer)thread).getAdditionalWorkDone();
                consumptionsCounter += ((Consumer)thread).getCounter();
            }
        }
        System.out.println("Producers additional work done: " + producersAdditionalWorkDone + " ms");
        System.out.println("Productions counter: " + productionsCounter);
        System.out.println("Consumers additional work done: " + consumersAdditionalWorkDone + " ms");
        System.out.println("Consumptions counter: " + consumptionsCounter);

        for (Thread thread : threads) {
            thread.stop();
        }
    }
}
