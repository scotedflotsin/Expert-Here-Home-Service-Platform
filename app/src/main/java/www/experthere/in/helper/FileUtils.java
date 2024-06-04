package www.experthere.in.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class FileUtils {

    public static String getFilePathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    public static double getFileSizeInKB(Context context, Uri fileUri) {
        long fileSizeInBytes = getFileSizeInBytes(context, fileUri);
        return fileSizeInBytes / 1024.0; // Convert bytes to kilobytes
    }

    public static double getFileSizeInMB(Context context, Uri fileUri) {
        long fileSizeInBytes = getFileSizeInBytes(context, fileUri);
        return fileSizeInBytes / (1024.0 * 1024.0); // Convert bytes to megabytes
    }

    private static long getFileSizeInBytes(Context context, Uri fileUri) {
        String filePath = getFilePathFromUri(context, fileUri);
        if (filePath != null) {
            File file = new File(filePath);
            return file.length();
        }
        return 0;
    }
}
