package kipinski.piotr;

import java.util.LinkedList;

public class Scheduler extends Thread {
    private final LinkedList<MethodRequest> queue = new LinkedList<>();

    public void run(){
        while (true) {
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    MethodRequest methodRequest = queue.pop();
                    if (methodRequest.guard()) {
                        methodRequest.execute();
                        //System.out.println("Executed request");
                    } else {
                        enqueue(methodRequest);
                    }
                }
            }
        }
    }

    public void enqueue(MethodRequest methodRequest){
        synchronized (queue) {
            queue.add(methodRequest);
        }
        //System.out.println("Added request");
    }
}
