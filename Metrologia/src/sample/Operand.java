package sample;

public class Operand {
    private String name;
    private int count;
    private int number;


    public Operand(String name) {
        this.name = name;
        incCount();
    }

    public Operand(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void incCount() {
        this.count++;
    }
}
