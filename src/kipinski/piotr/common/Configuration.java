package kipinski.piotr.common;

public class Configuration {
    public static final int RANDOM_SEED = 2137;

    public static final int BUFFER_SIZE = 1000;
    public static final double BUFFER_WORK_TIME_MULTIPLIER = 0.005f;

    public static final int MAX_PRODUCTION_SIZE = BUFFER_SIZE / 2;
    public static final int MAX_CONSUMPTION_SIZE = BUFFER_SIZE / 2;

    public static final boolean INFINITE_MODE = false;
    public static final int PRODUCTIONS_PER_PRODUCER = 4000; //doesn't work if INFINITE_MODE is true
    public static final int CONSUMPTIONS_PER_CONSUMER = 4000; //doesn't work if INFINITE_MODE is true
    public static final int TIMEOUT = 60000; //only works with INFINITE_MODE enabled


    public static final int ASYNC_TIME_QUANTUM = 10;
    public static final int PRODUCERS_NUM = 3;
    public static final int CONSUMERS_NUM = 3;
}
