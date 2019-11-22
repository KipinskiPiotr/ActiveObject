package kipinski.piotr;

import java.util.concurrent.CompletableFuture;

public class BufferProxy {
    private Scheduler scheduler;
    private Buffer activeObject;

    BufferProxy(){
        this.scheduler = new Scheduler();
        this.scheduler.start();
        this.activeObject = new Buffer();
    }

    public CompletableFuture<Integer> add(int number){
        CompletableFuture<Integer> future = new CompletableFuture<>();
        AddMethodRequest addMethodRequest = new AddMethodRequest(number, activeObject, future);
        scheduler.enqueue(addMethodRequest);

        return future;
    }

    public CompletableFuture<Integer> subtract(int number){
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SubtractMethodRequest subtractMethodRequest = new SubtractMethodRequest(number, activeObject, future);
        scheduler.enqueue(subtractMethodRequest);

        return future;
    }
}
