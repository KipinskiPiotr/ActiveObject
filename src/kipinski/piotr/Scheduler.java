package kipinski.piotr;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread {
    private final BlockingQueue<MethodRequest> mainQueue = new LinkedBlockingQueue<>();
    private final Queue<SubtractMethodRequest> deferredConsumersQueue = new LinkedList<>();

    public void run(){
        while (true) {
            SubtractMethodRequest subtractMethodRequest = deferredConsumersQueue.peek();
            if(subtractMethodRequest != null){
                if (subtractMethodRequest.guard()) {
                    subtractMethodRequest.execute();
                    deferredConsumersQueue.remove();
                    continue;
                }
            }
            try {
                MethodRequest methodRequest = mainQueue.take();
                if (methodRequest.guard()) {
                    methodRequest.execute();
                } else if (methodRequest instanceof SubtractMethodRequest) {
                    this.deferredConsumersQueue.add((SubtractMethodRequest) methodRequest);
                } else {
                    enqueue(methodRequest);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void enqueue(MethodRequest methodRequest){
        try {
            mainQueue.put(methodRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
