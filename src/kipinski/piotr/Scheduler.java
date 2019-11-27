package kipinski.piotr;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread {
    private final BlockingQueue<MethodRequest> blockingQueue = new LinkedBlockingQueue<>();
    private final Queue<MethodRequest> consumersQueue = new LinkedList<>();

    public void run(){
        while (true) {
            MethodRequest methodRequest = consumersQueue.poll();
            if(methodRequest != null){
                if (methodRequest.guard()) {
                    methodRequest.execute();
                    System.out.println("Executed request from consumersQueue");
                } else {
                    this.consumersQueue.add(methodRequest);
                }
            }
            try {
                methodRequest = blockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(methodRequest.guard()){
                methodRequest.execute();
                System.out.println("Executed request from blockingQueue");
            }else if(methodRequest.getClass().isInstance(AddMethodRequest.class)){
                this.consumersQueue.add(methodRequest);
            }else{
                enqueue(methodRequest);
            }
        }
    }

    void enqueue(MethodRequest methodRequest){
        try {
            blockingQueue.put(methodRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
