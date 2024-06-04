package www.experthere.in.helper;

import android.widget.TextView;

public class TextMaskUtil {

    public static void maskText(TextView textView, String originalText, int maxLength,String placeHolder) {
        String maskedText = originalText.length() > maxLength
                ? originalText.substring(0, maxLength - 2) + placeHolder
                : originalText;

        textView.setText(maskedText);
    }
}
