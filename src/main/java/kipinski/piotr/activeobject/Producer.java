package kipinski.piotr.activeobject;

import kipinski.piotr.common.Configuration;

import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer extends Thread {
    private BufferProxy bufferProxy;
    private Random random = new Random(Configuration.RANDOM_SEED);
    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger additionalWorkDone = new AtomicInteger();
    private int timeQuantum;

    Producer(BufferProxy bufferProxy, int timeQuantum) {
        this.bufferProxy = bufferProxy;
        this.timeQuantum = timeQuantum;
    }

    public void run() {
        if (Configuration.TIMED_MODE) {
            while (true) {
                produce();
            }
        } else {
            for (int i = 0; i < Configuration.PRODUCTIONS_PER_PRODUCER; i++) {
                produce();
            }
        }
    }

    public void produce() {
        Future future = bufferProxy.add(random.nextInt(Configuration.MAX_PRODUCTION_SIZE));
        while (!future.isDone()) {
            try {
                sleep(timeQuantum);
                additionalWorkDone.addAndGet(timeQuantum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    public int getAdditionalWorkDone() {
        return additionalWorkDone.get();
    }
}
