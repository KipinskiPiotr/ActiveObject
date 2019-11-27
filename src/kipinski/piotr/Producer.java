package kipinski.piotr;

import java.util.concurrent.CompletableFuture;

public class Producer extends BufferManipulator {
    private BufferProxy bufferProxy;

    Producer(BufferProxy bufferProxy) {
        this.bufferProxy = bufferProxy;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            addFutureResult(bufferProxy.add(1));
        }
        System.out.println(Thread.currentThread().getName() + " finished producing.");
    }

}
