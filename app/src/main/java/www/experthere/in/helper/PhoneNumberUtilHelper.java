package www.experthere.in.helper;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberUtilHelper {

    public static String extractCountryCode(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "");
            int countryCode = parsedPhoneNumber.getCountryCode();
            return "+"+ countryCode;
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
