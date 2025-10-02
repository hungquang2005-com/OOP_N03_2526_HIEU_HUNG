package model;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    public static String layThoiGianHienTai() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String dinhDang(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }

    public static long tinhKhoangCachPhut(Date batDau, Date ketThuc) {
        if (batDau == null || ketThuc == null) return 0;
        long hieu = ketThuc.getTime() - batDau.getTime();
        return hieu / (1000 * 60);
    }

    public static long tinhKhoangCachGio(Date batDau, Date ketThuc) {
        if (batDau == null || ketThuc == null) return 0;
        long hieu = ketThuc.getTime() - batDau.getTime();
        return hieu / (1000 * 60 * 60);
    }
}
