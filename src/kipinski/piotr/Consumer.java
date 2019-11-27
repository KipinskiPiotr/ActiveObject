package kipinski.piotr;

import java.util.Random;
import java.util.concurrent.Future;

public class Consumer extends Thread {
    private BufferProxy bufferProxy;
    private Random random = new Random();

    Consumer(BufferProxy bufferProxy){
        this.bufferProxy = bufferProxy;
    }

    public void run(){
        for(int i=0; i<10; i++){
            Future future = bufferProxy.subtract(random.nextInt(bufferProxy.getMaxNumber()/2));
            while (!future.isDone()){
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(Thread.currentThread().getName() + " finished consuming.");
    }
}
