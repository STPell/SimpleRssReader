package com.example.hellodroid;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageGetter implements Html.ImageGetter {

    private TextView textView = null;

    public ImageGetter(TextView target) {
        textView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        BitmapDrawablePlaceHolder drawable = new BitmapDrawablePlaceHolder();
        Picasso.get()
               .load(source)
               .placeholder(R.drawable.ic_launcher_background)
               .resize((int) Math.round(metrics.widthPixels - (metrics.xdpi * (6.0 / 25.4))), metrics.heightPixels)
               .centerInside()
               .onlyScaleDown()
               .into(drawable);
        return drawable;
    }

    private class BitmapDrawablePlaceHolder extends BitmapDrawable implements Target {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, width, height);
            setBounds(0, 0, width, height);
            if (textView != null) {
                textView.setText(textView.getText());
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setDrawable(new BitmapDrawable(textView.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.w("IMAGE", "Loading the image failed: " + e.toString());
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

    }
}

//public class ImageGetter implements Html.ImageGetter {
//
//    @Override
//    public Drawable getDrawable(String source) {
//        ImageLoader temp = RssHttpRequestQueue.getInstance().getImageLoader();
//        BitmapDrawable drawable = new BitmapDrawable();
//        temp.get(source, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                drawable = response.getBitmap();
//            }
//        });
//        return drawable;
//    }
//}
