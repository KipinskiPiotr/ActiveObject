package kipinski.piotr;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler extends Thread {
    private final Queue<MethodRequest> mainQueue = new LinkedList<>();
    private final Queue<SubtractMethodRequest> deferredConsumersQueue = new LinkedList<>();

    public void run(){
        while (true) {
            SubtractMethodRequest subtractMethodRequest = deferredConsumersQueue.poll();
            if(subtractMethodRequest != null){
                if (subtractMethodRequest.guard()) {
                    subtractMethodRequest.execute();
                } else {
                    this.deferredConsumersQueue.add(subtractMethodRequest);
                }
            }
            MethodRequest methodRequest;
            synchronized (mainQueue) {
                methodRequest = mainQueue.poll();
            }
            if(methodRequest != null) {
                if (methodRequest.guard()) {
                    methodRequest.execute();
                } else if (methodRequest instanceof SubtractMethodRequest) {
                    this.deferredConsumersQueue.add((SubtractMethodRequest) methodRequest);
                } else {
                    enqueue(methodRequest);
                }
            }
        }
    }

    void enqueue(MethodRequest methodRequest){
        synchronized (mainQueue) {
            mainQueue.add(methodRequest);
        }
    }
}
