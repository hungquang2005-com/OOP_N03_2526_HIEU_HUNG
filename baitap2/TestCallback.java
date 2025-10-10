package baitap2;
interface Incrementable {
    void increment();
}

class Callee implements Incrementable {
    private int count = 0;

    @Override
    public void increment() {
        count++;
        System.out.println("Callee incremented, count = " + count);
    }
}

class Caller {
    private Incrementable callbackReference;

    Caller(Incrementable cbr) {
        callbackReference = cbr;
    }

    void go() {
        callbackReference.increment(); 
    }
}


public class TestCallback {
    public static void main(String[] args) {
        Callee callee = new Callee();
        Caller caller = new Caller(callee);

        caller.go(); 
        caller.go(); 
        caller.go(); 
    }
}

