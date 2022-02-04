package main.system.yaml;

/**
 * Created by Alexander on 2/2/2022
 */
public class YamlEntity {
    int one;
    int two;
    int three;

    public YamlEntity() {
    }

    public YamlEntity(int one, int two) {
        this.one = one;
        this.two = two;
    }

    public YamlEntity(int one, int two, int three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public void setThree(int three) {
        this.three = three;
    }

    public int getThree() {
        return three;
    }
}
