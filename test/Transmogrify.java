abstract class Actor {
    abstract void act();
}

class HappyActor extends Actor {
    public void act() {
        System.out.println("I am a Happy Actor!");
    }
}

class SadActor extends Actor {
    public void act() {
        System.out.println("I am a Sad Actor...");
    }
}

class Stage {
    Actor a = new HappyActor(); 

    void change() { 
        a = new SadActor();    
    }

    void go() { 
        a.act();             
    }
}

public class Transmogrify {
    public static void main(String[] args) {
        Stage s = new Stage();
        s.go();       
        s.change(); 
        s.go();       
    }
}
