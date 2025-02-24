package attendance.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

public enum AttendancePolicy {
    OPERATING_START_TIME(LocalTime.of(8, 0)),
    OPERATING_END_TIME(LocalTime.of(23, 0)),
    MONDAY_EDUCATION_START_TIME(LocalTime.of(13, 0)),
    GENERAL_EDUCATION_START_TIME(LocalTime.of(10, 0)),
    MONDAY_LATE_TIME(LocalTime.of(13, 5)),
    GENERAL_LATE_TIME(LocalTime.of(10, 5)),
    MONDAY_ABSENCE_TIME(LocalTime.of(13, 30)),
    GENERAL_ABSENCE_TIME(LocalTime.of(10, 30))
    ;

    private final LocalTime time;

    AttendancePolicy(LocalTime time) {
        this.time = time;
    }

    public static boolean isCheckIn(DayOfWeek dayOfWeek, LocalTime attendanceTime) {
        if (dayOfWeek == DayOfWeek.MONDAY) {
            return attendanceTime.equals(OPERATING_START_TIME.time) ||
                    (attendanceTime.isAfter(OPERATING_START_TIME.time) && attendanceTime.isBefore(MONDAY_LATE_TIME.time))
                    || attendanceTime.equals(MONDAY_LATE_TIME.time);
        }

        return attendanceTime.equals(OPERATING_START_TIME.time) ||
                (attendanceTime.isAfter(OPERATING_START_TIME.time) && attendanceTime.isBefore(GENERAL_LATE_TIME.time))
                || attendanceTime.equals(GENERAL_LATE_TIME.time);
    }

    public static boolean isLate(DayOfWeek dayOfWeek, LocalTime attendanceTime) {
        if (dayOfWeek == DayOfWeek.MONDAY) {
            return attendanceTime.isAfter(OPERATING_START_TIME.time)
                    && attendanceTime.isBefore(MONDAY_ABSENCE_TIME.time) || attendanceTime.equals(MONDAY_ABSENCE_TIME.time);
        }

        return attendanceTime.isAfter(OPERATING_START_TIME.time)
                && attendanceTime.isBefore(GENERAL_ABSENCE_TIME.time) || attendanceTime.equals(GENERAL_ABSENCE_TIME.time);
    }
}
