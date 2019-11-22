package kipinski.piotr;

public class Buffer {
    private int number = 0;
    private int maxNumber = 1000;

    public int add(int number){
        this.number += number;
        return this.number;
    }
    public int subtract(int number){
        this.number -= number;
        return this.number;
    }

    public int getNumber(){
        return number;
    }

    public int getMaxNumber(){
        return maxNumber;
    }
}
