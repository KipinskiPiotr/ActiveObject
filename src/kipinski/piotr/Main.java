package kipinski.piotr;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        BufferProxy bufferProxy = new BufferProxy();

        BufferManipulator[] threads = new BufferManipulator[6];
        threads[0] = new Producer(bufferProxy);
        threads[1] = new Consumer(bufferProxy);
        threads[2] = new Producer(bufferProxy);
        threads[3] = new Consumer(bufferProxy);
        threads[4] = new Producer(bufferProxy);
        threads[5] = new Consumer(bufferProxy);

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].waitForResults();
        }

        System.out.println("End");

    }
}
