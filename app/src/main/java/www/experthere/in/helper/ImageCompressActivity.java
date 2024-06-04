package www.experthere.in.helper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import www.experthere.in.R;
import www.experthere.in.test.TestActivity;

public class ImageCompressActivity extends AppCompatActivity {
    Bundle bundle;
    String compressedImageNormalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_compress);


        bundle = getIntent().getExtras();
        if (bundle != null) {

            cropAndCompressImage(bundle.getString("originalUri"),
                    bundle.getInt("length", 4)
                    , bundle.getInt("height", 3));


        } else {

            CustomToastNegative.create(this,"No Data Sent");


            finish();
        }

    }

    private void cropAndCompressImage(String uri, int ratioLength, int ratioHeight) {

        CropImage.activity(Uri.parse(uri))
                .setMultiTouchEnabled(false)
                .setAspectRatio(ratioLength, ratioHeight)
                .setActivityTitle("Crop Image")
                .setBackgroundColor(getColor(R.color.transparent))
                .setOutputCompressQuality(60)
                .start(this);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (result != null) {

                compressedImageNormalPath = String.valueOf(result.getUri());

                Log.d("URIIIIII", "Compressed Uri " + compressedImageNormalPath);

                Bundle sendingBundle = new Bundle();

                if (bundle != null && bundle.getString("image", "none").matches("doc")) {


                    Intent resultIntent = new Intent();
                    sendingBundle.putString("croppedImageUri", compressedImageNormalPath);
                    sendingBundle.putString("image", "doc");
                    resultIntent.putExtras(sendingBundle);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                if (bundle != null && bundle.getString("image", "none").matches("logo")) {

                    Intent resultIntent = new Intent();
                    sendingBundle.putString("croppedImageUri", compressedImageNormalPath);
                    sendingBundle.putString("image", "logo");
                    resultIntent.putExtras(sendingBundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                if (bundle != null && bundle.getString("image", "none").matches("banner")) {

                    Intent resultIntent = new Intent();
                    sendingBundle.putString("croppedImageUri", compressedImageNormalPath);
                    sendingBundle.putString("image", "banner");
                    resultIntent.putExtras(sendingBundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }


            }
        }

    }


    public String getImageRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return contentUri.getPath(); // Fallback to the URI itself
    }


    int resumeCount = 0;

    @Override
    protected void onResume() {
        super.onResume();

        if (resumeCount == 1) {

            finish();

        } else {
            resumeCount++;

        }

    }
}