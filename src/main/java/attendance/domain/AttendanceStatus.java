package attendance.domain;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public enum AttendanceStatus {
    CHECKIN("출석"),
    ABSENCE("결석"),
    LATE("지각")
    ;

    private final String name;

    AttendanceStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AttendanceStatus determineStatus(LocalDateTime attendanceDateTime) {
        LocalTime attendanceTime = attendanceDateTime.toLocalTime();
        DayOfWeek dayOfWeek = attendanceDateTime.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.MONDAY) {
            return determineStatusByDayOfWeek(dayOfWeek, attendanceTime);
        }

        return determineStatusByDayOfWeek(dayOfWeek, attendanceTime);
    }

    private static AttendanceStatus determineStatusByDayOfWeek(DayOfWeek dayOfWeek, LocalTime attendanceTime) {
        if (AttendancePolicy.isCheckIn(dayOfWeek, attendanceTime)) {
            return CHECKIN;
        }

        if (AttendancePolicy.isLate(dayOfWeek, attendanceTime)) {
            return LATE;
        }

        return ABSENCE;
    }
}
