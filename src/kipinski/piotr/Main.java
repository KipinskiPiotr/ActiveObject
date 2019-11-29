package kipinski.piotr;

import kipinski.piotr.activeobject.MainAsynchronously;
import kipinski.piotr.synchronously.MainSynchronously;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MainAsynchronously.main(null);
        System.out.println();
        MainSynchronously.main(null);
    }
}
