package attendance.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class AttendanceTest {

    @MethodSource("generateAttendanceOfWeekend")
    @ParameterizedTest(name = "{index} : {2}")
    void 등교날짜가_주말이면_예외가_발생한다(LocalDate weekend, LocalTime attendanceTime, String message) {
        LocalDateTime attendanceDateTime = LocalDateTime.of(weekend, attendanceTime);

        assertThatThrownBy(() -> Attendance.of(attendanceDateTime))
            .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> generateAttendanceOfWeekend() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 12, 14), LocalTime.MIN, "토요일"),
                Arguments.of(LocalDate.of(2024, 12, 15), LocalTime.MIN, "일요일")
        );
    }

    @MethodSource("generateAttendanceOfWeekday")
    @ParameterizedTest(name = "{index} : {2}")
    void 등교날짜가_주말이아니면_출석을_한다(LocalDate weekday, LocalTime attendanceTime, String message) {
        LocalDateTime attendanceDateTime = LocalDateTime.of(weekday, attendanceTime);

        assertThatCode(() -> Attendance.of(attendanceDateTime))
            .doesNotThrowAnyException();
    }

    static Stream<Arguments> generateAttendanceOfWeekday() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 12, 9), LocalTime.MIN, "월요일"),
                Arguments.of(LocalDate.of(2024, 12, 10), LocalTime.MIN, "화요일")
        );
    }

    @CsvSource(value = {"13:5:CHECKIN", "13:6:LATE", "13:31:ABSENCE"}, delimiterString = ":")
    @ParameterizedTest
    void 월요일의_시간에따라_다른출석상태를_반환한다(int hour, int minute, AttendanceStatus expectedStatus) {
        LocalDate monday = LocalDate.of(2024, 12, 9);
        LocalTime time = LocalTime.of(hour, minute);
        LocalDateTime attendanceDateTime = LocalDateTime.of(monday, time);
        Attendance attendance = Attendance.of(attendanceDateTime);

        assertThat(attendance.getStatus()).isEqualTo(expectedStatus);
    }

    @CsvSource(value = {"10:5:CHECKIN", "10:6:LATE", "10:31:ABSENCE"}, delimiterString = ":")
    @ParameterizedTest
    void 화요일의_시간에따라_다른출석상태를_반환한다(int hour, int minute, AttendanceStatus expectedStatus) {
        LocalDate tuesday = LocalDate.of(2024, 12, 10);
        LocalTime time = LocalTime.of(hour, minute);
        LocalDateTime attendanceDateTime = LocalDateTime.of(tuesday, time);
        Attendance attendance = Attendance.of(attendanceDateTime);

        assertThat(attendance.getStatus()).isEqualTo(expectedStatus);
    }

    @Test
    void 등교날짜가_공휴일이면_예외가_발생한다() {
        LocalDate holiday = LocalDate.of(2025, 12, 25);
        LocalTime time = LocalTime.of(9, 59);
        LocalDateTime attendanceDateTime = LocalDateTime.of(holiday, time);

        assertThatThrownBy(() -> Attendance.of(attendanceDateTime))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 출석시간을_수정할수있고_수정된_시간에따라_상태가_변경된다() {
        LocalDate previousDate = LocalDate.of(2024, 12, 3);
        LocalTime previousTime = LocalTime.of(10, 7);
        LocalTime modifiedTime = LocalTime.of(9, 58);
        Attendance attendance = Attendance.of(LocalDateTime.of(previousDate, previousTime));

        attendance.modify(LocalDateTime.of(previousDate, modifiedTime));

        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.CHECKIN);
    }
}
