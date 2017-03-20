package cit.edu.paloma.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by charlie on 3/19/17.
 */

public class DateTimeUtils {
    public static String getReadableDateTime(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date(timestamp));
    }
}
