package kipinski.piotr;

public class Consumer extends BufferManipulator {
    private BufferProxy bufferProxy;

    Consumer(BufferProxy bufferProxy) {
        this.bufferProxy = bufferProxy;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            addFutureResult(bufferProxy.subtract(1));
        }
        System.out.println(Thread.currentThread().getName() + " finished consuming.");
    }
}
