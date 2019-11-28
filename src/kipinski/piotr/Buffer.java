package kipinski.piotr;

import static java.lang.Thread.sleep;

class Buffer {
    private int number = 0;
    private int maxNumber;
    private int workMultiplier;

    Buffer(int maxNumber, int workMultiplier){
        this.maxNumber = maxNumber;
        this.workMultiplier = workMultiplier;
    }

    int add(int number){
        this.number += number;
        try {
            sleep(number*workMultiplier);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.number;
    }

    int subtract(int number){
        this.number -= number;
        try {
            sleep(number*workMultiplier);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.number;
    }

    int getNumber(){
        return number;
    }

    int getMaxNumber(){
        return maxNumber;
    }
}
