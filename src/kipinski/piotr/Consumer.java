package kipinski.piotr;

import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer extends Thread {
    private BufferProxy bufferProxy;
    private Random random = new Random();
    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger additionalWorkDone = new AtomicInteger();
    private int timeQuantum;

    Consumer(BufferProxy bufferProxy, int timeQuantum){
        this.bufferProxy = bufferProxy;
        this.timeQuantum = timeQuantum;
    }

    public void run(){
        while(true){
            Future future = bufferProxy.subtract(random.nextInt(bufferProxy.getMaxNumber()/2));
            while (!future.isDone()){
                try {
                    sleep(timeQuantum);
                    additionalWorkDone.addAndGet(timeQuantum);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            counter.incrementAndGet();
        }
    }

    public int getCounter() {
        return counter.get();
    }

    public int getAdditionalWorkDone() {
        return additionalWorkDone.get();
    }

}
