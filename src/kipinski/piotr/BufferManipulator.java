package kipinski.piotr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class BufferManipulator extends Thread {
    List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();

    void addFutureResult(CompletableFuture<Integer> completableFuture) {
        completableFutures.add(completableFuture);
    }

    public void waitForResults() {
        completableFutures.forEach(e -> {
            try {
                Integer result = e.get();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        });
    }
}
