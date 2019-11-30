package kipinski.piotr.synchronously;

import kipinski.piotr.common.Buffer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedBuffer {
    private ReentrantLock firstCostumerLock = new ReentrantLock();
    private ReentrantLock firstProducerLock = new ReentrantLock();
    private ReentrantLock rest = new ReentrantLock();

    private Condition producer = rest.newCondition();
    private Condition consumer = rest.newCondition();

    private Buffer buffer;

    public SynchronizedBuffer(int bufferSize, double workMultiplier) {
        this.buffer = new Buffer(bufferSize, workMultiplier);
    }

    void produce(int id, int valueToAdd) throws InterruptedException {
        firstCostumerLock.lock();
        rest.lock();

        while (buffer.getNumber() + valueToAdd > buffer.getMaxNumber()) {
            producer.await();
        }

        buffer.add(valueToAdd);

        consumer.signal();
        //System.out.println(System.nanoTime() + " Producer " + id + " produced " + valueToAdd + " elements ("  + ")");

        rest.unlock();
        firstCostumerLock.unlock();
    }

    void consume(int id, int valueToSubtract) throws InterruptedException {
        firstProducerLock.lock();
        rest.lock();

        while (buffer.getNumber() - valueToSubtract < 0) {
            consumer.await();
        }

        buffer.subtract(valueToSubtract);

        producer.signal();
        //System.out.println(System.nanoTime() + " Consumer " + id + " consumed " + valueToSubtract + " elements ("  + ")");

        rest.unlock();
        firstProducerLock.unlock();
    }

    public int getMaxNumber() {
        return buffer.getMaxNumber();
    }
}


/*
nauczyc sie tego co na zajeciach przyklad na zakleszczenie na hasWaiters
prod:
lock1 {
    lock2{
        //reszta
        await(x);
        //zawieszenie zwalnia ktory lock. Na pewno zwalnia lock2, a lock1 nie (lock1 nadal posiadam)
        //lock1 = firstConsumer
        //lock2 = restConsumers
    }
}

cons:
lock3 {
    lock4{
        await(y);

        signal(x);
    }
}

zastosowac ten uklad do uzyskania tego samego rozwiazania. zamiast 4 condition -> 4 locki i 2 condition

 */