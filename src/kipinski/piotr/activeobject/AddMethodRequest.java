package kipinski.piotr.activeobject;

import kipinski.piotr.common.Buffer;

import java.util.concurrent.CompletableFuture;

public class AddMethodRequest implements MethodRequest{
    private CompletableFuture<Integer> future;
    private Buffer activeObject;
    private int number;

    AddMethodRequest(int number, Buffer activeObject, CompletableFuture<Integer> future){
        this.number = number;
        this.activeObject = activeObject;
        this.future = future;
    }

    public boolean guard(){
        return activeObject.getNumber() + number <= activeObject.getMaxNumber();
    }

    public void execute(){
        Integer result = activeObject.add(number);

        future.complete(result);
    }
}
