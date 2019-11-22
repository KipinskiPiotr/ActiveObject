package kipinski.piotr;

class Buffer {
    private int number = 0;
    private int maxNumber = 1000;

    int add(int number){
        this.number += number;
        return this.number;
    }
    int subtract(int number){
        this.number -= number;
        return this.number;
    }

    int getNumber(){
        return number;
    }

    int getMaxNumber(){
        return maxNumber;
    }
}
