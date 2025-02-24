package attendance.domain;

import attendance.constant.Holiday;
import attendance.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class Attendance {

    private LocalDateTime attendanceDateTime;
    private AttendanceStatus status;

    private Attendance(LocalDateTime attendanceDateTime) {
        validateAttendanceDate(attendanceDateTime.toLocalDate());
        this.status = AttendanceStatus.determineStatus(attendanceDateTime);
        this.attendanceDateTime = attendanceDateTime;
    }

    public static Attendance of(LocalDateTime attendanceDateTime) {
        return new Attendance(attendanceDateTime);
    }

    private void validateAttendanceDate(LocalDate attendDate) {
        if (DateUtil.isWeekend(attendDate) || Holiday.isHoliday(attendDate)) {
            throw new IllegalArgumentException(String.format("%n[ERROR] %s은 등교일이 아닙니다.", attendDate.format(
                    DateTimeFormatter.ofPattern(DateUtil.DATE_FORMATTER, Locale.KOREAN))));
        }
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void modify(LocalDateTime modifiedDateTime) {
        this.status = AttendanceStatus.determineStatus(modifiedDateTime);
        this.attendanceDateTime = modifiedDateTime;
    }

    public LocalDateTime getAttendanceDateTime() {
        return attendanceDateTime;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Attendance that = (Attendance) object;
        return Objects.equals(attendanceDateTime, that.attendanceDateTime) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceDateTime, status);
    }
}
