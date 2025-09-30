import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    // Lấy thời gian hiện tại dưới dạng String
    public static String layThoiGianHienTai() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    // Chuyển Date thành String theo format "dd/MM/yyyy HH:mm:ss"
    public static String dinhDang(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }

    // Tính khoảng cách giữa 2 Date theo phút
    public static long tinhKhoangCachPhut(Date batDau, Date ketThuc) {
        if (batDau == null || ketThuc == null) return 0;
        long hieu = ketThuc.getTime() - batDau.getTime();
        return hieu / (1000 * 60);
    }

    // Tính khoảng cách giữa 2 Date theo giờ
    public static long tinhKhoangCachGio(Date batDau, Date ketThuc) {
        if (batDau == null || ketThuc == null) return 0;
        long hieu = ketThuc.getTime() - batDau.getTime();
        return hieu / (1000 * 60 * 60);
    }
}
