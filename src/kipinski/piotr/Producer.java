package kipinski.piotr;

public class Producer extends Thread {
    private BufferProxy bufferProxy;

    Producer(BufferProxy bufferProxy){
        this.bufferProxy = bufferProxy;
    }

    public void run(){
        for(int i=0; i<1000; i++){
            bufferProxy.add(1);
        }
        System.out.println(Thread.currentThread().getName() + " finished producing.");
    }
}
