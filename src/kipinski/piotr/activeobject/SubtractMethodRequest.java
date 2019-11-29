package kipinski.piotr.activeobject;

import kipinski.piotr.common.Buffer;

import java.util.concurrent.CompletableFuture;

public class SubtractMethodRequest implements MethodRequest {
    private CompletableFuture<Integer> future;
    private Buffer activeObject;
    private int number;

    SubtractMethodRequest(int number, Buffer activeObject, CompletableFuture<Integer> future){
        this.number = number;
        this.activeObject = activeObject;
        this.future = future;
    }

    public boolean guard(){
        return activeObject.getNumber() - number >= 0;
    }

    public void execute(){
        int result = activeObject.subtract(number);
        future.complete(result);
    }
}
