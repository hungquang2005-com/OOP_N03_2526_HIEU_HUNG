package Animal;
interface Animal {
    void sound();   
    void eat();    
}

class Dog implements Animal {
    public void sound() {
        System.out.println("Dog barks: Woof woof!");
    }

    public void eat() {
        System.out.println("Dog eats bone.");
    }
}

class Cat implements Animal {
    public void sound() {
        System.out.println("Cat meows: Meow meow!");
    }

    public void eat() {
        System.out.println("Cat eats fish.");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal a1 = new Dog();  
        Animal a2 = new Cat();

        a1.sound();
        a1.eat();

        a2.sound();
        a2.eat();
    }
}

