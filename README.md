# A11_231A290134 — Thực đơn có Menu, Dialog & Snackbar

Bài thực hành INT4211 – Lập trình trên các thiết bị di động, **Lab A11**.
Đây là bản **nâng cấp của Lab A9**: giữ nguyên `ListView` + `BaseAdapter` + `ViewHolder`,
bỏ phần nhập liệu ở đầu màn hình và thay bằng **options menu**, **context menu**,
**hộp thoại tự thiết kế**, **AlertDialog xác nhận** và **Snackbar Hoàn tác**.

Repo: https://github.com/nvquangggit-sys/A11_231A290134

## Chức năng

- **Options menu** nạp từ `res/menu/menu_main.xml`, xử lý trong `onOptionsItemSelected`
  bằng chuỗi `if – else` (không dùng `switch`): Thêm món (＋), Sắp xếp theo tên,
  Sắp xếp theo giá, Lọc theo mức giá, Xóa tất cả.
- **Context menu** (`res/menu/menu_context.xml`) mở bằng cách giữ lâu một dòng.
  Vị trí dòng lấy từ `AdapterView.AdapterContextMenuInfo.position`;
  tiêu đề menu là đúng tên món. Listener `setOnItemLongClickListener` của A9 đã bị **xóa hẳn**
  để không nuốt sự kiện giữ lâu.
- **AlertDialog xác nhận trước khi xóa**, lời hỏi nêu rõ tên món:
  `Bạn có chắc muốn xóa "Cơm tấm sườn" khỏi thực đơn?` — nút **Xóa** / **Hủy**.
- **Hộp thoại tự thiết kế** (`dialog_mon_an.xml`, `setView`) cho Thêm/Sửa.
  Listener của nút Lưu được **gán lại sau `show()`**, nên khi dữ liệu sai hộp thoại
  **không đóng**: thiếu tên → “Chưa nhập tên món”; giá không phải số → “Giá phải là số”;
  giá ≤ 0 → “Giá phải lớn hơn 0”.
- **Snackbar Hoàn tác** sau khi xóa: `danhSach.add(viTri, daXoa)` trả món về **đúng vị trí cũ**,
  không phải cuối danh sách.
- **NC1 — SearchView** trên thanh tiêu đề: lọc ngay khi gõ, không phân biệt dấu
  (`bun` tìm ra “Bún chả Hà Nội”).
- **NC2 — hộp thoại chọn mức giá** bằng `setSingleChoiceItems`:
  Tất cả / Dưới 30.000 đ / Từ 30.000 đ đến 45.000 đ / Trên 45.000 đ;
  chỉ khi bấm **Áp dụng** mới lọc, bấm **Hủy** thì giữ nguyên.
- **NC3 — `MaterialAlertDialogBuilder`** dùng cho *mọi* hộp thoại (chi tiết, xác nhận,
  tự thiết kế, chọn mức giá) để có giao diện Material 3.
- Danh sách gốc `danhSach` tách khỏi danh sách đang hiển thị `hienThi`; mọi thay đổi
  đi qua `locVaCapNhat()` để adapter và chip thống kê luôn khớp.
- Danh sách sống sót qua lần xoay màn hình (`onSaveInstanceState` + `Serializable`).

## Cấu hình

| Trường | Giá trị |
| --- | --- |
| Package / applicationId | `vn.edu.vhu.ltdd.a11dialog` |
| Language | Java + XML |
| Minimum SDK | API 24 |
| compileSdk / targetSdk | 36 / 36 |
| Theme | `Theme.A11Dialog` (`Theme.Material3.Light.NoActionBar` + `MaterialToolbar`) |
| AVD kiểm thử | Pixel 6 API 34 (Android 14, x86_64, 1080 × 2400) |

## Build

```bash
./gradlew assembleDebug            # Windows: gradlew.bat assembleDebug
# hoặc mở project bằng Android Studio và bấm Run
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Yêu cầu: JDK 17 đi kèm Android Studio, Android SDK 36. Ảnh món ăn nằm trong
`app/src/main/res/drawable/mon1.jpg … mon5.jpg` (300 × 300, mỗi ảnh dưới 35 KB).

## Cấu trúc

```
app/src/main/java/vn/edu/vhu/ltdd/a11dialog/
├── MonAn.java            # Lớp dữ liệu + dinhDangGia() + khongDau() cho NC1
├── MonAnAdapter.java     # BaseAdapter 4 phương thức, ViewHolder, nền dòng > 45.000 đ
└── MainActivity.java     # Options menu, context menu, 4 loại hộp thoại, Snackbar, lọc/sắp xếp
app/src/main/res/layout/
├── activity_main.xml     # MaterialToolbar + hướng dẫn + chip thống kê + ListView + tvTrong
├── item_mon_an.xml       # Khuôn của MỘT dòng
└── dialog_mon_an.xml     # Nội dung hộp thoại Thêm/Sửa (3 ô EditText)
app/src/main/res/menu/
├── menu_main.xml         # ＋, SearchView (NC1), 3 mục trong menu ba chấm
└── menu_context.xml      # Sửa món / Xóa
docs/
├── screenshots/          # 13 ảnh: 10 cho 8 kịch bản + 1 NC1 + 2 NC2
├── logs/logcat_a11.txt   # Logcat thật của TAG A11_231A290134
├── A11_231A290134_demo.mp4          # Video demo 1 phút 02 giây
├── BaoCao_LabA11_231A290134.docx    # Báo cáo (Word)
└── BaoCao_LabA11_231A290134.pdf     # Báo cáo (PDF, 17 trang)
```

## Kịch bản kiểm thử (8 kịch bản của đề)

| # | Thao tác | Kết quả quan sát | Ảnh |
| --- | --- | --- | --- |
| 1 | Mở app, nhìn thanh tiêu đề | Có biểu tượng ＋, kính lúp và nút ba chấm; 5 món mẫu, chip “5 món · 190.000 đ” | `a11_home.png` |
| 2 | Mở menu ba chấm | Đủ Sắp xếp theo tên / Sắp xếp theo giá / Lọc theo mức giá / Xóa tất cả | `a11_menu.png` |
| 3 | Chọn Sắp xếp theo giá | Danh sách xếp tăng dần 25 → 30 → 40 → 45 → 50 (nghìn), Toast “Đã sắp xếp theo giá tăng dần” | `a11_sort_toast.png` |
| 4 | Giữ lâu dòng thứ 3 (Cơm tấm sườn) | Context menu hiện, tiêu đề đúng “Cơm tấm sườn”, có Sửa món / Xóa | `a11_ctx.png` |
| 5 | Chọn Xóa trong context menu | Hộp thoại “Xác nhận xóa” nêu đúng tên: `Bạn có chắc muốn xóa "Cơm tấm sườn" khỏi thực đơn?` | `a11_confirm.png` |
| 6 | Bấm Hủy trong hộp thoại | Không xóa gì, danh sách vẫn đủ 5 món · 190.000 đ | `a11_huy.png` |
| 7 | Bấm Xóa rồi bấm HOÀN TÁC | Còn 4 món + Snackbar “Đã xóa Cơm tấm sườn”; Hoàn tác đưa món về **đúng vị trí thứ 3** | `a11_snackbar.png`, `a11_undo.png` |
| 8 | Mở dialog Sửa, xóa trắng ô tên rồi bấm Lưu | Ô tên báo đỏ “Chưa nhập tên món”, hộp thoại **không đóng**, dữ liệu vẫn còn | `a11_sua.png`, `a11_err_ten.png` |

Ảnh của bài nâng cao: `a11_search.png` (NC1 — gõ `bun` còn 1/5 món),
`a11_gia_dialog.png` + `a11_gia_loc.png` (NC2 — chọn “Trên 45.000 đ” rồi Áp dụng).

> Ghi chú về ảnh: trong nhiều ảnh, dòng “Bún chả Hà Nội” (50.000 đ) có nền vàng nhạt vì
> adapter tô nền cho món giá **trên 45.000 đ** — đây là hiệu ứng giữ lại từ Lab A9, không
> phải dòng đang được chọn.

Logcat thật (`docs/logs/logcat_a11.txt`, TAG `A11_231A290134`) xác nhận đúng vị trí dòng:

```
D A11_231A290134: Context menu Xóa: Cơm tấm sườn (vị trí 2)
D A11_231A290134: Đã xóa Cơm tấm sườn tại vị trí 2
D A11_231A290134: Hoàn tác: Cơm tấm sườn về vị trí 2
```

## Video demo

`docs/A11_231A290134_demo.mp4` — 1 phút 02 giây, đúng trình tự đề yêu cầu:
Sắp xếp theo giá → giữ lâu **Sửa** (xem hộp thoại điền sẵn) → giữ lâu **Xóa** → **Hủy**
(danh sách giữ nguyên) → giữ lâu **Xóa** → **Xóa** → **HOÀN TÁC** (món trở lại vị trí thứ 3).

## Báo cáo

Báo cáo Word/PDF đầy đủ (bảng 8 kịch bản kèm ảnh, 3 câu hỏi phân tích, mô tả NC1–NC3,
câu hỏi ôn tập) nằm trong `docs/BaoCao_LabA11_231A290134.docx` / `.pdf` (17 trang).

## Nguồn ảnh

Ảnh món ăn lấy từ Wikimedia Commons, giấy phép CC BY-SA 3.0/4.0 — xem [CREDITS.md](CREDITS.md).
