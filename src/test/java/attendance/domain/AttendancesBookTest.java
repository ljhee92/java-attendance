package attendance.domain;

import attendance.file.AttendanceFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AttendancesBookTest {
    public static final String path = "src/test/resources/testAttendances.csv";
    public static final String DELIMITER = ",";
    public static final int CREW_INDEX = 0;
    private static final int DATETIME_INDEX = 1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AttendancesBook attendancesBook;
    private Crews crews;

    @BeforeEach
    void setUp() {
        List<String> contentsByLine = AttendanceFileReader.readContents(path);
        attendancesBook = new AttendancesBook(new HashMap<>());
        crews = new Crews(new HashSet<>());
        contentsByLine.forEach(line -> {
            Crew crew = new Crew(line.split(DELIMITER)[CREW_INDEX]);
            crews.addCrew(crew);
            LocalDateTime attendanceTime = LocalDateTime.parse(line.split(DELIMITER)[DATETIME_INDEX], FORMATTER);
            attendancesBook.addAttendance(crew, Attendance.of(attendanceTime));
        });
    }

    @Test
    void _12월14일_기준으로_빙티의_출석기록은_10개이다() {
        Crew crew = new Crew("빙티");
        LocalDate localDate = LocalDate.of(2024, 12, 14);
        assertThat(attendancesBook.getAttendancesOfCrew(crew, localDate)).hasSize(10);
    }

    @ParameterizedTest
    @CsvSource(value = {"빙티:INTERVIEW", "쿠키:REMOVAL"}, delimiterString = ":")
    void 지각횟수와_결석횟수로_제적위험자를_판단한다(String nickName, Penalty expected) {
        Crew crew = new Crew(nickName);
        LocalDate localDate = LocalDate.of(2024, 12, 14);
        List<Attendance> attendances = attendancesBook.getAttendancesOfCrew(crew, localDate);
        int absenceCount = attendancesBook.countAttendanceStatus(attendances, AttendanceStatus.ABSENCE);
        int lateCount = attendancesBook.countAttendanceStatus(attendances, AttendanceStatus.LATE);
        Penalty penalty = Penalty.determine(absenceCount, lateCount);
        assertThat(penalty).isEqualTo(expected);
    }
}
