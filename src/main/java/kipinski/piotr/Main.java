package kipinski.piotr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kipinski.piotr.activeobject.MainAsynchronously;
import kipinski.piotr.common.Configuration;
import kipinski.piotr.synchronously.MainSynchronously;

import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(args[0]);
        tryLoading(rootNode,
                (node) -> Configuration.TIMED_MODE = node.get("timedMode").asBoolean());
        tryLoading(rootNode,
                (node) -> Configuration.TEST_TIME = node.get("testTime").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.BUFFER_WORK_TIME_MULTIPLIER = node.get("bufferWorkTimeMultiplier").asDouble());
        tryLoading(rootNode,
                (node) -> Configuration.PRODUCTIONS_PER_PRODUCER = node.get("productionsPerProducer").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.CONSUMPTIONS_PER_CONSUMER = node.get("consumptionsPerConsumer").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.BUFFER_SIZE = node.get("bufferSize").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.PRODUCERS_NUM = node.get("producersNum").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.CONSUMERS_NUM = node.get("consumersNum").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.ASYNC_TIME_QUANTUM = node.get("asyncTimeQuantum").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.SYNCHRONIZED_ADDITIONAL_WORK = node.get("synchronizedAdditionalWork").asInt());
        tryLoading(rootNode,
                (node) -> Configuration.JSON_OUTPUT = node.get("jsonOutput").asBoolean());

        //Configuration.print();
        if ("synchronously".equals(rootNode.get("type").asText())) {
            MainSynchronously.main(null);
        } else if ("asynchronously".equals(rootNode.get("type").asText())) {
            MainAsynchronously.main(null);
        }
        System.exit(0);
    }

    private static void tryLoading(JsonNode rootNode, Consumer<JsonNode> consumer) {
        try {
            consumer.accept(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
