package kipinski.piotr;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler extends Thread {
    private final Queue<MethodRequest> mainQueue = new LinkedList<>();
    private final Queue<SubtractMethodRequest> deferredConsumers = new LinkedList<>();

    public void run() {
        while (true) {
            if (!deferredConsumers.isEmpty()) {
                SubtractMethodRequest methodRequest = deferredConsumers.remove();
                if (methodRequest.guard()) {
                    methodRequest.execute();
                } else {
                    deferredConsumers.add(methodRequest);
                }
            }
            synchronized (mainQueue) {
                if (!mainQueue.isEmpty()) {
                    MethodRequest methodRequest = mainQueue.remove();
                    if (methodRequest.guard()) {
                        methodRequest.execute();
                    } else {
                        if (methodRequest instanceof AddMethodRequest) {
                            mainQueue.add(methodRequest);
                        } else if (methodRequest instanceof SubtractMethodRequest) {
                            deferredConsumers.add((SubtractMethodRequest) methodRequest);
                        }
                    }
                }
            }
        }
    }

    void enqueue(MethodRequest methodRequest) {
        synchronized (mainQueue) {
            mainQueue.add(methodRequest);
        }
        //System.out.println("Added request");
    }
}
