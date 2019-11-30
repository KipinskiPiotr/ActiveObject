package kipinski.piotr.activeobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kipinski.piotr.common.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class MainAsynchronously {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        BufferProxy buffer = new BufferProxy(Configuration.BUFFER_SIZE, Configuration.BUFFER_WORK_TIME_MULTIPLIER);
        int timeQuantum = Configuration.ASYNC_TIME_QUANTUM;

        List<Thread> threads = new ArrayList<>();
        IntStream.range(0, Configuration.PRODUCERS_NUM)
                .forEach(e -> threads.add(new Producer(buffer, timeQuantum)));
        IntStream.range(0, Configuration.CONSUMERS_NUM)
                .forEach(e -> threads.add(new Consumer(buffer, timeQuantum)));
        Collections.shuffle(threads, new Random(Configuration.RANDOM_SEED));

        if (!Configuration.JSON_OUTPUT)
            System.out.println("Starting asynchronously test");
        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        if (Configuration.TIMED_MODE) {
            sleep(Configuration.TEST_TIME);
            int producersAdditionalWorkDone = 0;
            int productionsCounter = 0;
            int consumersAdditionalWorkDone = 0;
            int consumptionsCounter = 0;
            for (Thread thread : threads) {
                if (thread instanceof Producer) {
                    producersAdditionalWorkDone += ((Producer) thread).getAdditionalWorkDone();
                    productionsCounter += ((Producer) thread).getCounter();
                } else {
                    consumersAdditionalWorkDone += ((Consumer) thread).getAdditionalWorkDone();
                    consumptionsCounter += ((Consumer) thread).getCounter();
                }
            }

            if (Configuration.JSON_OUTPUT){
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode rootNode = objectMapper.createObjectNode();
                rootNode.put("productionsCounter", productionsCounter);
                rootNode.put("consumptionsCounter", consumptionsCounter);
                rootNode.put("producersAdditionalWorkDone", producersAdditionalWorkDone);
                rootNode.put("consumersAdditionalWorkDone", consumersAdditionalWorkDone);
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
            } else {
                System.out.println("Producers additional work done: " + producersAdditionalWorkDone + " ms");
                System.out.println("Productions counter: " + productionsCounter);
                System.out.println("Consumers additional work done: " + consumersAdditionalWorkDone + " ms");
                System.out.println("Consumptions counter: " + consumptionsCounter);
            }

            for (Thread thread : threads) {
                thread.stop();
            }
        } else {
            for (Thread thread : threads) {
                thread.join();
            }
            long stopTime = System.currentTimeMillis();

            int producersAdditionalWorkDone = 0;
            int productionsCounter = 0;
            int consumersAdditionalWorkDone = 0;
            int consumptionsCounter = 0;
            for (Thread thread : threads) {
                if (thread instanceof Producer) {
                    producersAdditionalWorkDone += ((Producer) thread).getAdditionalWorkDone();
                    productionsCounter += ((Producer) thread).getCounter();
                } else {
                    consumersAdditionalWorkDone += ((Consumer) thread).getAdditionalWorkDone();
                    consumptionsCounter += ((Consumer) thread).getCounter();
                }
            }

            if (Configuration.JSON_OUTPUT){
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode rootNode = objectMapper.createObjectNode();
                rootNode.put("finishTime", (stopTime - startTime));
                rootNode.put("productionsCounter", productionsCounter);
                rootNode.put("consumptionsCounter", consumptionsCounter);
                rootNode.put("producersAdditionalWorkDone", producersAdditionalWorkDone);
                rootNode.put("consumersAdditionalWorkDone", consumersAdditionalWorkDone);
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
            } else {
                System.out.println("Program finished in " + (stopTime - startTime));
                System.out.println("Producers additional work done: " + producersAdditionalWorkDone + " ms");
                System.out.println("Productions counter: " + productionsCounter);
                System.out.println("Consumers additional work done: " + consumersAdditionalWorkDone + " ms");
                System.out.println("Consumptions counter: " + consumptionsCounter);
            }
        }
    }
}
