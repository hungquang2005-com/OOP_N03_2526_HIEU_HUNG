import java.util.Date;

import practice.Time;

public class TestTime {
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("Thời gian hiện tại: " + Time.layThoiGianHienTai());

        Date batDau = new Date();
        System.out.println("Khách đến lúc: " + Time.dinhDang(batDau));

        Thread.sleep(2000);

        Date ketThuc = new Date();
        System.out.println("Khách rời lúc: " + Time.dinhDang(ketThuc));

        long phut = Time.tinhKhoangCachPhut(batDau, ketThuc);
        long gio = Time.tinhKhoangCachGio(batDau, ketThuc);

        System.out.println("Khách ngồi trong: " + phut + " phút (" + gio + " giờ).");
    }
}
