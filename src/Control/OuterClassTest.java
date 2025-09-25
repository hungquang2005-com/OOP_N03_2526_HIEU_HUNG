package Control;
public class OuterClassTest {
    int var = 100; 
    public class InnerClassTest {
        public InnerClassTest() {
            System.out.println(">>> InnerClassTest constructor: var = " + var);
        }
    }

    public static class InnerStaticClass {
        public InnerStaticClass() {
            System.out.println(">>> InnerStaticClass constructor: var = 200");
        }
    }
}

