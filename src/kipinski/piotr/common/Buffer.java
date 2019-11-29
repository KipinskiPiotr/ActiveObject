package kipinski.piotr.common;

import static java.lang.Thread.sleep;

public class Buffer {
    private int number = 0;
    private int maxNumber;
    private int workMultiplier;

    public Buffer(int maxNumber, int workMultiplier){
        this.maxNumber = maxNumber;
        this.workMultiplier = workMultiplier;
    }

    public int add(int number){
        this.number += number;
        try {
            sleep(number*workMultiplier);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.number;
    }

    public int subtract(int number){
        this.number -= number;
        try {
            sleep(number*workMultiplier);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.number;
    }

    public int getNumber(){
        return number;
    }

    public int getMaxNumber(){
        return maxNumber;
    }
}
