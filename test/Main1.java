import Interfaces.MyCat;

public class Main1 {
    public static void main(String[] args) {
        MyCat tom = new MyCat("Tom");

        tom.sound();
        tom.walk();
        tom.jump();
        tom.catchMouse();
        tom.scratch();
    }
}
