package vn.edu.vhu.ltdd.a11dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter tự viết: biến mỗi đối tượng MonAn thành một dòng trong ListView.
 *
 * BaseAdapter bắt buộc cài đặt 4 phương thức: getCount, getItem, getItemId, getView.
 * Trong đó getView là nơi mọi việc thực sự diễn ra.
 */
public class MonAnAdapter extends BaseAdapter {

    private static final String TAG = "A11_231A290134/Adapter";

    /** Giữ sẵn tham chiếu tới các View của một dòng để khỏi findViewById lại. */
    private static class ViewHolder {
        ImageView imgAnh;
        TextView tvTen, tvMoTa, tvGia;
    }

    private final Context context;
    private final List<MonAn> danhSach;
    private final LayoutInflater inflater;

    /** Đếm số lần inflate thật sự — dùng cho bài thí nghiệm Logcat. */
    private int soLanInflate;

    /** Đếm số lần dùng lại convertView, log thưa để Logcat dễ đọc. */
    private int soLanTaiSuDung;

    public MonAnAdapter(Context context, List<MonAn> danhSach) {
        this.context = context;
        this.danhSach = danhSach;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return danhSach.size();          // ListView hỏi: có bao nhiêu dòng?
    }

    @Override
    public Object getItem(int position) {
        return danhSach.get(position);   // dữ liệu của dòng thứ position
    }

    @Override
    public long getItemId(int position) {
        return position;                 // chưa có id thật thì dùng vị trí
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Không có dòng cũ để tái sử dụng → phải "thổi" layout mới (tốn kém)
            convertView = inflater.inflate(R.layout.item_mon_an, parent, false);
            holder = new ViewHolder();
            holder.imgAnh = convertView.findViewById(R.id.imgAnh);
            holder.tvTen = convertView.findViewById(R.id.tvTen);
            holder.tvMoTa = convertView.findViewById(R.id.tvMoTa);
            holder.tvGia = convertView.findViewById(R.id.tvGia);
            convertView.setTag(holder);          // gắn kèm vào dòng để lần sau lấy ra
            soLanInflate++;
            Log.d(TAG, "inflate dòng mới #" + soLanInflate + " (vị trí " + position + ")");
        } else {
            // Dòng này vừa cuộn khỏi màn hình → dùng lại, chỉ cần đổi nội dung
            holder = (ViewHolder) convertView.getTag();
            soLanTaiSuDung++;
            if (soLanTaiSuDung % 20 == 1) {
                Log.d(TAG, "tái sử dụng convertView — lần thứ " + soLanTaiSuDung);
            }
        }

        MonAn mon = danhSach.get(position);
        holder.imgAnh.setImageResource(mon.getAnhResId());
        holder.tvTen.setText(mon.getTen());
        holder.tvMoTa.setText(mon.getMoTa());
        holder.tvGia.setText(MonAn.dinhDangGia(mon.getGia()));

        // NC2: món giá trên 45.000 đ có nền nhạt. Phải gán lại màu cho CẢ hai
        // trường hợp, nếu không màu sẽ "dính" sang dòng khác khi cuộn.
        if (mon.getGia() > 45000) {
            convertView.setBackgroundColor(context.getColor(R.color.item_highlight));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
