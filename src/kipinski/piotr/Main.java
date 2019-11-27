package kipinski.piotr;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BufferProxy bufferProxy = new BufferProxy();

        Thread[] threads = new Thread[6];
        threads[0] = new Producer(bufferProxy);
        threads[1] = new Producer(bufferProxy);
        threads[2] = new Producer(bufferProxy);
        threads[3] = new Consumer(bufferProxy);
        threads[4] = new Consumer(bufferProxy);
        threads[5] = new Consumer(bufferProxy);

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("End");
    }
}
