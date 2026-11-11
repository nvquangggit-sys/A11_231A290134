package vn.edu.vhu.ltdd.a11dialog;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Lớp mô tả một món ăn. Tách dữ liệu ra khỏi giao diện.
 *
 * implements Serializable để cả danh sách sống sót qua lần xoay màn hình
 * (MainActivity lưu vào savedInstanceState).
 */
public class MonAn implements Serializable {

    private String ten;
    private String moTa;
    private int gia;        // đơn vị đồng — dùng int cho tiền tệ, KHÔNG dùng double
    private int anhResId;   // R.drawable.xxx

    public MonAn(String ten, String moTa, int gia, int anhResId) {
        this.ten = ten;
        this.moTa = moTa;
        this.gia = gia;
        this.anhResId = anhResId;
    }

    public String getTen() { return ten; }

    public String getMoTa() { return moTa; }

    public int getGia() { return gia; }

    public int getAnhResId() { return anhResId; }

    public void setTen(String ten) { this.ten = ten; }

    public void setMoTa(String moTa) { this.moTa = moTa; }

    public void setGia(int gia) { this.gia = gia; }

    /**
     * Bỏ dấu tiếng Việt để tìm kiếm không phân biệt dấu:
     * "Bún chả" → "bun cha", nhờ đó gõ "bun" vẫn tìm ra.
     */
    public static String khongDau(String s) {
        String tmp = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return tmp.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
    }

    /** 45000 → "45.000 đ". Không cần thư viện ngoài. */
    public static String dinhDangGia(int gia) {
        String s = String.valueOf(gia);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (s.length() - i) % 3 == 0) sb.append('.');
            sb.append(s.charAt(i));
        }
        return sb + " đ";
    }
}
