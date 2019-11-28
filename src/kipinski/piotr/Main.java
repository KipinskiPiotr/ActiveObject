package kipinski.piotr;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BufferProxy bufferProxy = new BufferProxy(1000, 1);
        int timeQuantum = 10;

        Thread[] threads = new Thread[6];
        threads[0] = new Producer(bufferProxy, timeQuantum);
        threads[1] = new Producer(bufferProxy, timeQuantum);
        threads[2] = new Producer(bufferProxy, timeQuantum);
        threads[3] = new Consumer(bufferProxy, timeQuantum);
        threads[4] = new Consumer(bufferProxy, timeQuantum);
        threads[5] = new Consumer(bufferProxy, timeQuantum);

        for (Thread thread : threads) {
            thread.start();
        }
        sleep(10000);
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

        System.exit(0);

    }
}
