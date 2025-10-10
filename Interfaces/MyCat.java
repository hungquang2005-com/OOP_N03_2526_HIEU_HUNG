package Interfaces;

public class MyCat implements LegInterface, HandInterface {
    private String name;

    public MyCat(String name) {
        this.name = name;
    }

    public void sound() {
        System.out.println(name + " says: Meow!");
    }

    @Override
    public void walk() {
        System.out.println(name + " is walking gracefully.");
    }

    @Override
    public void jump() {
        System.out.println(name + " jumps high!");
    }

    @Override
    public void catchMouse() {
        System.out.println(name + " catches a mouse!");
    }

    @Override
    public void scratch() {
        System.out.println(name + " scratches the sofa!");
    }
}
