import Control.OuterClassTest;
public class Main2 {
    public static void main(String[] args) {
     
        OuterClassTest oct = new OuterClassTest();

        OuterClassTest.InnerClassTest ict = oct.new InnerClassTest();
        System.out.println("Đã tạo inner class thường: " + ict);

        OuterClassTest.InnerStaticClass isc = new OuterClassTest.InnerStaticClass();
        System.out.println("Đã tạo inner static class: " + isc);
    }
}
