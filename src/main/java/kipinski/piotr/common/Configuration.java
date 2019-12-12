package kipinski.piotr.common;

public class Configuration {
    public static final int RANDOM_SEED = 2137;

    public static int BUFFER_SIZE = 1000;
    public static double BUFFER_WORK_TIME_MULTIPLIER = 0.005f;

    public static int MAX_PRODUCTION_SIZE = BUFFER_SIZE / 2;
    public static int MAX_CONSUMPTION_SIZE = BUFFER_SIZE / 2;

    public static boolean TIMED_MODE = false;
    public static int PRODUCTIONS_PER_PRODUCER = 4000; //doesn't work if INFINITE_MODE is true
    public static int CONSUMPTIONS_PER_CONSUMER = 4000; //doesn't work if INFINITE_MODE is true
    public static int TEST_TIME = 60000; //only works with TIMED_MODE enabled


    public static int ASYNC_TIME_QUANTUM = 10;
    public static int PRODUCERS_NUM = 3;
    public static int CONSUMERS_NUM = 3;

    public static int SYNCHRONIZED_ADDITIONAL_WORK = 0;

    public static boolean JSON_OUTPUT = false;

    public static void print() {
        System.out.println("BUFFER_SIZE:" + BUFFER_SIZE);
        System.out.println("BUFFER_WORK_TIME_MULTIPLIER:" + BUFFER_WORK_TIME_MULTIPLIER);
        System.out.println("MAX_PRODUCTION_SIZE:" + MAX_PRODUCTION_SIZE);
        System.out.println("MAX_CONSUMPTION_SIZE:" + MAX_CONSUMPTION_SIZE);
        System.out.println("TIMED_MODE:" + TIMED_MODE);
        System.out.println("PRODUCTIONS_PER_PRODUCER:" + PRODUCTIONS_PER_PRODUCER);
        System.out.println("CONSUMPTIONS_PER_CONSUMER:" + CONSUMPTIONS_PER_CONSUMER);
        System.out.println("TEST_TIME:" + TEST_TIME);
        System.out.println("ASYNC_TIME_QUANTUM:" + ASYNC_TIME_QUANTUM);
        System.out.println("PRODUCERS_NUM:" + PRODUCERS_NUM);
        System.out.println("CONSUMERS_NUM:" + CONSUMERS_NUM);
        System.out.println("JSON_OUTPUT:" + JSON_OUTPUT);
        System.out.println("SYNCHRONIZED_ADDITIONAL_WORK:" + SYNCHRONIZED_ADDITIONAL_WORK);

    }
}
