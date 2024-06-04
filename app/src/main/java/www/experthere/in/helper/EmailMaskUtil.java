package www.experthere.in.helper;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class EmailMaskUtil {

    public static void maskEmailAndSetToTextView(String email, TextView textView) {
        if (email == null || email.length() < 6) {
            textView.setText(email);
            return;
        }

        int atIndex = email.indexOf('@');
        if (atIndex < 0 || atIndex >= email.length() - 1) {
            textView.setText(email);
            return;
        }

        int prefixLength = Math.min(3, atIndex); // Ensure we don't exceed the actual length
        String prefix = email.substring(0, prefixLength);
        int domainLength = Math.min(email.length() - (atIndex + 1), 10); // Adjust the number to mask more or fewer characters
        String domain = email.substring(atIndex + 1, atIndex + 1 + domainLength);
        String maskedDomain = "xxx";
        int suffixLength = Math.min(2, email.length() - (atIndex + 1 + domainLength)); // Ensure we don't exceed the actual length
        String suffix = email.substring(email.length() - suffixLength);

        String maskedEmail = prefix + maskedDomain + "@" + domain + suffix;

        SpannableStringBuilder spannable = new SpannableStringBuilder(maskedEmail);
        int maskColor = Color.BLUE; // Change to your desired color
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(maskColor);
        spannable.setSpan(colorSpan, prefixLength, prefixLength + maskedDomain.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }
}
