package attendance.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtil {

    public static final String DATE_FORMATTER = "MM월 dd일 EEE요일";

    private DateUtil() {}

    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY
            || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
