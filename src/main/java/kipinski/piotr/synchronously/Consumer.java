package kipinski.piotr.synchronously;

import kipinski.piotr.common.Configuration;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer extends Thread {
    private SynchronizedBuffer buffer;
    private Random random = new Random(Configuration.RANDOM_SEED);
    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger additionalWorkDone = new AtomicInteger();
    private int timeQuantum;
    private int additionalWork;

    Consumer(SynchronizedBuffer buffer, int timeQuantum, int additionalWork) {
        this.buffer = buffer;
        this.timeQuantum = timeQuantum;
        this.additionalWork = additionalWork;
    }

    public void run() {
        if (Configuration.TIMED_MODE) {
            while (true) {
                consume();
            }
        } else {
            for (int i = 0; i < Configuration.CONSUMPTIONS_PER_CONSUMER; i++) {
                consume();
            }
        }
    }

    public void consume() {
        try {
            buffer.consume(0, random.nextInt(Configuration.MAX_CONSUMPTION_SIZE));
            if (additionalWork != 0) {
                sleep(additionalWork);
            }
            counter.incrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCounter() {
        return counter.get();
    }

    public int getAdditionalWorkDone() {
        return additionalWorkDone.get();
    }

}
