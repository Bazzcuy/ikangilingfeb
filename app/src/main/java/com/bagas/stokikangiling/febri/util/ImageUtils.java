package com.bagas.stokikangiling.febri.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bagas.stokikangiling.febri.R;

public final class ImageUtils {
    private ImageUtils() {}
    public static void tampilkan(ImageView view, String value) {
        if (value == null || value.trim().isEmpty()) {
            view.setImageResource(R.drawable.ic_image_placeholder);
            return;
        }
        try {
            if (value.startsWith("res:")) {
                Context context = view.getContext();
                int id = context.getResources().getIdentifier(value.substring(4), "drawable", context.getPackageName());
                view.setImageResource(id == 0 ? R.drawable.ic_image_placeholder : id);
            } else {
                view.setImageURI(Uri.parse(value));
                if (view.getDrawable() == null) view.setImageResource(R.drawable.ic_image_placeholder);
            }
        } catch (Exception ignored) {
            view.setImageResource(R.drawable.ic_image_placeholder);
        }
    }
}
