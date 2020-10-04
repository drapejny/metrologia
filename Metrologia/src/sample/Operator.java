package sample;

public class Operator {
    public static int i;

    private String name;
    private String regex;
    private int count;
    private int number;

    public Operator(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public Operator(String name) {
        this.name = name;
        this.count++;
    }
    public Operator(String name, int count){
        this.name = name;
        this.count = count;
    }

    public void incCount() {
        this.count++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

