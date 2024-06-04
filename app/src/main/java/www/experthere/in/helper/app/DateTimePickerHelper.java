package www.experthere.in.helper.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import www.experthere.in.R;

public class DateTimePickerHelper {

    private final Context context;

    public DateTimePickerHelper(Context context) {
        this.context = context;
    }

    public void showDateTimePicker(final DateTimePickerCallback callback) {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                int hour = currentDate.get(Calendar.HOUR_OF_DAY);
                int minute = currentDate.get(Calendar.MINUTE);
                showTimePickerDialog(selectedYear, selectedMonth, selectedDay, hour, minute, callback);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final int year, final int month, final int day, final int hour, final int minute, final DateTimePickerCallback callback) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(

                context, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                Calendar selectedDateTime = Calendar.getInstance();
                selectedDateTime.set(year, month, day, selectedHour, selectedMinute);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                String formattedDateTime = dateFormat.format(selectedDateTime.getTime());

                // Convert AM/PM to uppercase
                formattedDateTime = formattedDateTime.replace("am", "AM").replace("pm", "PM");

                // Invoke the callback with the selected date and time
                callback.onDateTimeSelected(formattedDateTime);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    public interface DateTimePickerCallback {
        void onDateTimeSelected(String formattedDateTime);
    }
}
