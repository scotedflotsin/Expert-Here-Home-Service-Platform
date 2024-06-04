package www.experthere.in.helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import www.experthere.in.R;

public class ImagePickerBottomSheet extends BottomSheetDialogFragment {

    private  final int REQUEST_GALLERY;
    private ImageSelectionListener imageSelectionListener;

    Uri currentPhotoPath;
    private final int REQUEST_IMAGE_CAPTURE;

    public ImagePickerBottomSheet(int REQUEST_GALLERY, int REQUEST_IMAGE_CAPTURE) {
        this.REQUEST_GALLERY = REQUEST_GALLERY;
        this.REQUEST_IMAGE_CAPTURE = REQUEST_IMAGE_CAPTURE;
    }

    public interface ImageSelectionListener {
        void onImageSelected(Uri imageUri);

        void onImagePath(Uri path);
    }

    public void setImageSelectionListener(ImageSelectionListener listener) {
        this.imageSelectionListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_imagepick, container, false);

        ImageView galleryButton = view.findViewById(R.id.galleryButton);
        ImageView cameraButton = view.findViewById(R.id.cameraButton);


        galleryButton.setOnClickListener(v -> openGallery());
        cameraButton.setOnClickListener(v -> openCamera());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {


        dispatchTakePictureIntent();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    sendSelectedImage(selectedImageUri);

                    Log.d("pathuri", "onActivityResult: " + getImageRealPathFromURI(selectedImageUri));


                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {


                Log.d("pathuri", "onActivityResult: " + currentPhotoPath);
                Log.d("pathuri", "onActivityResult: " + getImageRealPathFromURI(currentPhotoPath));

//                sendSelectedImagePath(getImageRealPathFromURI(Uri.parse(currentPhotoPath)));
                sendSelectedImagePath(currentPhotoPath);

            }
        }
    }

    private void sendSelectedImage(Uri imageUri) {

        if (imageSelectionListener != null) {
            imageSelectionListener.onImageSelected(imageUri);
            dismiss();
        }

    }

    private void sendSelectedImagePath(Uri imageUri) {

        if (imageSelectionListener != null) {
            imageSelectionListener.onImagePath(imageUri);
            dismiss();
        }

    }

    public String getImageRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return contentUri.getPath(); // Fallback to the URI itself
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        currentPhotoPath = Uri.fromFile(image);
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                CustomToastNegative.create(requireActivity(),"Error: "+ex.getMessage() );

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        "www.experthere.in.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
