package com.appyware.filters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Drawable[] layers = new Drawable[2];

    String imgPath1;
    int prog, width, height;
    SeekBar seekBar;
    LinearLayout linearLayout, linearLayout1, linearLayout2;
    private static final int ACTION_TAKE_PHOTO_B = 1;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView imageView, imageView1;
    private Bitmap bitmap, filterBitmap, bitmap1;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        //imgPath = albumF.getAbsolutePath();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


        /* Associate the Bitmap to the ImageView */
        //imageView.setImageBitmap(bitmap);
        //imageView1.setImageBitmap(bitmap);

        Resources r = getResources();
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        layers[0] = d;
        layers[1] = r.getDrawable(R.drawable.filred);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        imageView.setImageDrawable(layerDrawable);

        imageView.setVisibility(View.VISIBLE);
        /*BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

        bitmap1 = bitmapDrawable.getBitmap();
        filterBitmap = Bitmap.createBitmap(bitmap1.getWidth(),
                bitmap1.getHeight(), bitmap1.getConfig());
        height = bitmap1.getHeight();
        width = bitmap1.getWidth();*/
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.VISIBLE);



    }

    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    /* To call camera and take photo */
    public void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        imgPath1 = f.getAbsolutePath();
        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            //galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    /* Function to apply effect to bitmap */
    public void applyeffect(View view, int progress) {

        /*for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int p = bitmap1.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                int alpha = Color.alpha(p);

                // Effect1
                r = progress * 255 + r;
                g = progress * 255 + g;
                b = progress * 255 + b;

                filterBitmap.setPixel(i, j, Color.argb(alpha, r, g, b));
            }
        }
        imageView.setImageBitmap(filterBitmap);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        imageView = (ImageView) findViewById(R.id.pic);
        imageView1 = (ImageView) findViewById(R.id.pic1);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearlayout1);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearlayout2);

        imageView.setVisibility(View.INVISIBLE);

        linearLayout.setVisibility(View.INVISIBLE);
        linearLayout1.setVisibility(View.INVISIBLE);
        linearLayout2.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        seekBar.setMax(255);

        /* To check seek bar value when moved */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

               // prog = progress;
                layers[1].setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Apply effect when seek bar stopped
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //View v = null;
               // applyeffect(v, prog);

            }
        });

    }


    public void camera(View v) {
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, bitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (bitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        imageView.setImageBitmap(bitmap);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.VISIBLE
        );
    }
}

