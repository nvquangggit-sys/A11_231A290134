package vn.edu.vhu.ltdd.a11dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Lab A11 — nâng cấp app A9 bằng Options menu, Context menu, Dialog, Toast và Snackbar.
 *
 * Bài nâng cao đã làm:
 *   NC1 — SearchView trên menu lọc danh sách khi đang gõ.
 *   NC2 — hộp thoại chọn mức giá bằng setSingleChoiceItems.
 *   NC3 — dùng MaterialAlertDialogBuilder cho mọi hộp thoại.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A11_231A290134";
    private static final String KEY_DANH_SACH = "key_danh_sach";

    /** NC2: bốn mức giá trong hộp thoại chọn mức giá. */
    private static final int GIA_TAT_CA = 0;
    private static final int GIA_DUOI_30 = 1;
    private static final int GIA_30_DEN_45 = 2;
    private static final int GIA_TREN_45 = 3;

    /** Toàn bộ thực đơn — dữ liệu gốc, không bị lọc. */
    private final List<MonAn> danhSach = new ArrayList<>();

    /** Danh sách đang hiển thị sau khi lọc theo từ khóa (NC1) và mức giá (NC2). */
    private final List<MonAn> hienThi = new ArrayList<>();

    private static final int[] ANH = {
            R.drawable.mon1, R.drawable.mon2, R.drawable.mon3,
            R.drawable.mon4, R.drawable.mon5
    };

    private MonAnAdapter adapter;
    private ListView lvMonAn;
    private TextView tvTrong, tvThongKe;
    private SearchView searchView;
    private MenuItem itemTimKiem;
    private String tuKhoa = "";
    private int mucGia = GIA_TAT_CA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View noiDung = findViewById(R.id.noiDung);
        final int pad = getResources().getDimensionPixelSize(R.dimen.screen_padding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Thanh tiêu đề kéo dài xuống dưới thanh trạng thái
            toolbar.setPadding(bars.left, bars.top, bars.right, 0);
            noiDung.setPadding(pad + bars.left, pad, pad + bars.right, pad + bars.bottom);
            return insets;
        });

        lvMonAn = findViewById(R.id.lvMonAn);
        tvTrong = findViewById(R.id.tvTrong);
        tvThongKe = findViewById(R.id.tvThongKe);

        // Khôi phục dữ liệu sau khi xoay màn hình; lần đầu mở thì nạp 5 món mẫu.
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_DANH_SACH)) {
            ArrayList<MonAn> daLuu =
                    (ArrayList<MonAn>) savedInstanceState.getSerializable(KEY_DANH_SACH);
            if (daLuu != null) danhSach.addAll(daLuu);
        } else {
            taoDuLieuMau();
        }

        adapter = new MonAnAdapter(this, hienThi);
        lvMonAn.setAdapter(adapter);
        lvMonAn.setEmptyView(tvTrong);

        // Đăng ký context menu: từ đây, giữ lâu một dòng sẽ mở menu ngữ cảnh.
        // KHÔNG đăng ký setOnItemLongClickListener nữa — listener đó sẽ nuốt sự kiện
        // giữ lâu và context menu không bao giờ hiện.
        registerForContextMenu(lvMonAn);

        // Chạm một dòng: mở hộp thoại xem chi tiết (AlertDialog chỉ có nút Đóng).
        lvMonAn.setOnItemClickListener((parent, view, position, id) ->
                hienChiTiet(hienThi.get(position)));

        locVaCapNhat();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_DANH_SACH, new ArrayList<>(danhSach));
    }

    private void taoDuLieuMau() {
        danhSach.add(new MonAn("Phở bò tái", "Nước dùng ninh xương 8 giờ", 45000, R.drawable.mon1));
        danhSach.add(new MonAn("Bún chả Hà Nội", "Chả nướng than hoa", 50000, R.drawable.mon2));
        danhSach.add(new MonAn("Cơm tấm sườn", "Sườn nướng mật ong", 40000, R.drawable.mon3));
        danhSach.add(new MonAn("Bánh mì thịt nguội", "Pate nhà làm", 25000, R.drawable.mon4));
        danhSach.add(new MonAn("Trà đào cam sả", "Mát lạnh, ít đường", 30000, R.drawable.mon5));
    }

    // ===================================================================
    // 1. OPTIONS MENU — menu của cả màn hình, nằm trên thanh tiêu đề
    // ===================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;    // true = hiện menu; trả false thì menu không xuất hiện
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemTimKiem = menu.findItem(R.id.action_tim_kiem);
        if (itemTimKiem != null) {
            searchView = (SearchView) itemTimKiem.getActionView();
            searchView.setQueryHint(getString(R.string.hint_tim));
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();   // ẩn bàn phím, giữ nguyên kết quả lọc
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    tuKhoa = newText.trim();
                    locVaCapNhat();
                    return true;
                }
            });
            itemTimKiem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                    // Đóng ô tìm kiếm = bỏ lọc theo từ khóa
                    if (!tuKhoa.isEmpty()) searchView.setQuery("", false);
                    return true;
                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Dùng if–else, KHÔNG dùng switch: từ AGP 8, R.id không còn là hằng số biên dịch.
        if (id == R.id.action_them) {
            hienDialogThemSua(null);
            return true;
        } else if (id == R.id.action_sap_xep_ten) {
            sapXepTheoTen();
            return true;
        } else if (id == R.id.action_sap_xep_gia) {
            sapXepTheoGia();
            return true;
        } else if (id == R.id.action_loc_gia) {
            hienDialogChonMucGia();
            return true;
        } else if (id == R.id.action_xoa_het) {
            xacNhanXoaHet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===================================================================
    // 2. CONTEXT MENU — menu của MỘT dòng, mở bằng cách giữ lâu
    // ===================================================================

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvMonAn) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            MonAn mon = hienThi.get(info.position);
            menu.setHeaderTitle(mon.getTen());     // tiêu đề = đúng tên món được giữ lâu
            getMenuInflater().inflate(R.menu.menu_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Vị trí dòng được giữ lâu nằm trong ContextMenuInfo, không phải trong item.
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) return super.onContextItemSelected(item);

        MonAn mon = hienThi.get(info.position);
        int id = item.getItemId();
        if (id == R.id.ctx_sua) {
            Log.d(TAG, "Context menu Sửa: " + mon.getTen() + " (vị trí " + info.position + ")");
            hienDialogThemSua(mon);
            return true;
        } else if (id == R.id.ctx_xoa) {
            Log.d(TAG, "Context menu Xóa: " + mon.getTen() + " (vị trí " + info.position + ")");
            xacNhanXoaMot(mon);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // ===================================================================
    // 3. DIALOG
    // ===================================================================

    /** Hộp thoại thông báo đơn giản: chỉ có nội dung và một nút Đóng. */
    private void hienChiTiet(MonAn mon) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(mon.getTen())
                .setMessage(getString(R.string.chi_tiet_format,
                        mon.getMoTa(), MonAn.dinhDangGia(mon.getGia())))
                .setPositiveButton(R.string.dong, null)   // null = chỉ đóng hộp thoại
                .show();
    }

    /** Hộp thoại xác nhận: việc xóa chỉ xảy ra khi người dùng bấm Xóa. */
    private void xacNhanXoaMot(MonAn mon) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.xac_nhan_xoa)
                .setMessage(getString(R.string.hoi_xoa, mon.getTen()))   // nêu rõ tên món
                .setPositiveButton(R.string.xoa, (d, w) -> xoaCoHoanTac(mon))
                .setNegativeButton(R.string.huy, null)
                .show();
    }

    private void xacNhanXoaHet() {
        if (danhSach.isEmpty()) {
            Toast.makeText(this, R.string.danh_sach_trong, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.xac_nhan_xoa)
                .setMessage(getString(R.string.hoi_xoa_het, danhSach.size()))
                .setPositiveButton(R.string.xoa_het, (d, w) -> {
                    final List<MonAn> banSaoLuu = new ArrayList<>(danhSach);
                    danhSach.clear();
                    locVaCapNhat();
                    Snackbar.make(lvMonAn, R.string.da_xoa_het, Snackbar.LENGTH_LONG)
                            .setAction(R.string.hoan_tac, v -> {
                                danhSach.addAll(banSaoLuu);
                                locVaCapNhat();
                            })
                            .show();
                })
                .setNegativeButton(R.string.huy, null)
                .show();
    }

    /** Xóa một món nhưng cho phép hoàn tác trong vài giây. */
    private void xoaCoHoanTac(MonAn mon) {
        final int viTri = danhSach.indexOf(mon);
        if (viTri < 0) return;

        final MonAn daXoa = danhSach.remove(viTri);
        locVaCapNhat();
        Log.d(TAG, "Đã xóa " + daXoa.getTen() + " tại vị trí " + viTri);

        Snackbar.make(lvMonAn, getString(R.string.da_xoa, daXoa.getTen()), Snackbar.LENGTH_LONG)
                .setAction(R.string.hoan_tac, v -> {
                    danhSach.add(viTri, daXoa);       // trả về đúng vị trí cũ
                    locVaCapNhat();
                    int hien = hienThi.indexOf(daXoa);
                    if (hien >= 0) lvMonAn.smoothScrollToPosition(hien);
                    Log.d(TAG, "Hoàn tác: " + daXoa.getTen() + " về vị trí " + viTri);
                })
                .show();
    }

    /**
     * Hộp thoại tự thiết kế: dùng setView để nhét một layout riêng vào.
     * monSua == null nghĩa là thêm mới, ngược lại là sửa chính đối tượng đó.
     */
    private void hienDialogThemSua(MonAn monSua) {
        final boolean themMoi = (monSua == null);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_mon_an, null, false);
        EditText edtTen = v.findViewById(R.id.edtTen);
        EditText edtMoTa = v.findViewById(R.id.edtMoTa);
        EditText edtGia = v.findViewById(R.id.edtGia);

        if (!themMoi) {
            edtTen.setText(monSua.getTen());
            edtMoTa.setText(monSua.getMoTa());
            edtGia.setText(String.valueOf(monSua.getGia()));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(themMoi ? R.string.them_mon : R.string.sua_mon)
                .setView(v)
                .setPositiveButton(R.string.luu, null)   // gán null rồi ghi đè ở dưới
                .setNegativeButton(R.string.huy, null)
                .create();

        dialog.show();

        // Mẹo quan trọng: gán lại OnClickListener SAU khi show() thì hộp thoại
        // sẽ KHÔNG tự đóng khi dữ liệu nhập chưa hợp lệ.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(btn -> {
            String ten = edtTen.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();
            String giaStr = edtGia.getText().toString().trim();

            if (ten.isEmpty()) {
                edtTen.setError(getString(R.string.err_ten));
                edtTen.requestFocus();
                return;      // không đóng hộp thoại
            }
            int gia;
            try {
                gia = Integer.parseInt(giaStr);
            } catch (NumberFormatException e) {
                edtGia.setError(getString(R.string.err_gia));
                edtGia.requestFocus();
                return;
            }
            if (gia <= 0) {
                edtGia.setError(getString(R.string.err_gia_duong));
                edtGia.requestFocus();
                return;
            }

            if (themMoi) {
                MonAn monMoi = new MonAn(ten, moTa.isEmpty() ? getString(R.string.moi_them) : moTa,
                        gia, ANH[danhSach.size() % ANH.length]);
                boLoc();                         // bỏ lọc để thấy món vừa thêm
                danhSach.add(0, monMoi);
                locVaCapNhat();
                lvMonAn.smoothScrollToPosition(0);
                Log.d(TAG, "Đã thêm: " + ten + " — tổng " + danhSach.size() + " món");
            } else {
                monSua.setTen(ten);
                monSua.setMoTa(moTa);
                monSua.setGia(gia);
                locVaCapNhat();
                Log.d(TAG, "Đã sửa: " + ten);
            }
            dialog.dismiss();   // tự tay đóng khi mọi thứ đã hợp lệ
            Toast.makeText(this, themMoi
                            ? getString(R.string.da_them, ten)
                            : getString(R.string.da_sua, ten),
                    Toast.LENGTH_SHORT).show();
        });
    }

    // ===================================================================
    // 4. SẮP XẾP & LỌC
    // ===================================================================

    private void sapXepTheoTen() {
        // Collator tiếng Việt sắp đúng thứ tự a, ă, â, b, c… thay vì theo mã Unicode.
        Collator collator = Collator.getInstance(new Locale("vi", "VN"));
        Collections.sort(danhSach, (a, b) -> collator.compare(a.getTen(), b.getTen()));
        locVaCapNhat();
        lvMonAn.setSelection(0);
        Toast.makeText(this, R.string.da_sap_xep_ten, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Đã sắp xếp theo tên");
    }

    private void sapXepTheoGia() {
        Collections.sort(danhSach, (a, b) -> Integer.compare(a.getGia(), b.getGia()));
        locVaCapNhat();
        lvMonAn.setSelection(0);
        Toast.makeText(this, R.string.da_sap_xep_gia, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Đã sắp xếp theo giá tăng dần");
    }

    /** NC2: hộp thoại chọn mức giá bằng setSingleChoiceItems. */
    private void hienDialogChonMucGia() {
        final String[] muc = getResources().getStringArray(R.array.muc_gia);
        final int[] luaChon = {mucGia};      // biến tạm, chỉ áp dụng khi bấm Áp dụng

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.chon_muc_gia)
                .setSingleChoiceItems(muc, mucGia, (d, which) -> luaChon[0] = which)
                .setPositiveButton(R.string.ap_dung, (d, w) -> {
                    mucGia = luaChon[0];
                    locVaCapNhat();
                    Toast.makeText(this,
                            getString(R.string.da_loc_gia, muc[mucGia]),
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Lọc mức giá: " + muc[mucGia]);
                })
                .setNegativeButton(R.string.huy, null)   // Hủy = giữ nguyên mức giá cũ
                .show();
    }

    // ===================================================================
    // 5. LỌC & HIỂN THỊ
    // ===================================================================

    /** Lọc theo tên (NC1) và mức giá (NC2), rồi báo cho adapter và cập nhật thống kê. */
    private void locVaCapNhat() {
        hienThi.clear();
        String khoa = MonAn.khongDau(tuKhoa);
        for (MonAn mon : danhSach) {
            if (!MonAn.khongDau(mon.getTen()).contains(khoa)) continue;
            if (!khopMucGia(mon)) continue;
            hienThi.add(mon);
        }
        adapter.notifyDataSetChanged();
        tvTrong.setText(danhSach.isEmpty()
                ? R.string.danh_sach_trong
                : R.string.khong_tim_thay);
        capNhatThongKe();
    }

    /** NC2: một món có nằm trong mức giá đang chọn hay không. */
    private boolean khopMucGia(MonAn mon) {
        int gia = mon.getGia();
        switch (mucGia) {
            case GIA_DUOI_30:    return gia < 30000;
            case GIA_30_DEN_45:  return gia >= 30000 && gia <= 45000;
            case GIA_TREN_45:    return gia > 45000;
            default:             return true;
        }
    }

    /** Bỏ mọi bộ lọc (dùng sau khi thêm món để chắc chắn món mới hiện ra). */
    private void boLoc() {
        tuKhoa = "";
        mucGia = GIA_TAT_CA;
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
        if (itemTimKiem != null && itemTimKiem.isActionViewExpanded()) {
            itemTimKiem.collapseActionView();
        }
    }

    /** Tổng số món và tổng giá trị thực đơn. */
    private void capNhatThongKe() {
        int tong = 0;
        for (MonAn mon : danhSach) tong += mon.getGia();
        String tien = MonAn.dinhDangGia(tong);
        if (tuKhoa.isEmpty() && mucGia == GIA_TAT_CA) {
            tvThongKe.setText(getString(R.string.thong_ke, danhSach.size(), tien));
        } else {
            tvThongKe.setText(getString(R.string.thong_ke_loc,
                    hienThi.size(), danhSach.size(), tien));
        }
    }
}
