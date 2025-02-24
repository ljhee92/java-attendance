package attendance.controller;

import attendance.constant.Holiday;
import attendance.domain.Attendance;
import attendance.domain.AttendanceStatus;
import attendance.domain.AttendancesBook;
import attendance.domain.Crew;
import attendance.domain.Crews;
import attendance.domain.Penalty;
import attendance.file.AttendanceFileReader;
import attendance.util.DateUtil;
import attendance.view.InputView;
import attendance.view.OutputView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class AttendanceController {

    private static final String path = "src/main/resources/attendances.csv";
    private static final String DELIMITER = ",";
    private static final int CREW_INDEX = 0;
    private static final int DATETIME_INDEX = 1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final LocalDate SYSTEM_DATE = LocalDate.now();

    private AttendancesBook attendancesBook;
    private Crews crews;

    public void run() {
        initialize();
        while (true) {
            String inputFunction = InputView.readFunction();
            try {
                performFunction(inputFunction);
                if (inputFunction.equals("Q")) {
                    break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void performFunction(String inputFunction) {
        if (inputFunction.equals("1")) {
            validateAttendanceDate(SYSTEM_DATE);
            recordAttendance();
        }
        if (inputFunction.equals("2")) {
            modifyAttendance();
        }
        if (inputFunction.equals("3")) {
            checkAttendanceRecordOfCrew();
        }
        if (inputFunction.equals("4")) {
            OutputView.printPenaltyOfCrews(crews.getCrews(), attendancesBook);
        }
    }

    private void initialize() {
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

    private void validateAttendanceDate(LocalDate attendDate) {
        if (DateUtil.isWeekend(attendDate) || Holiday.isHoliday(attendDate)) {
            throw new IllegalArgumentException(String.format("%n[ERROR] %s은 등교일이 아닙니다.", attendDate.format(
                    DateTimeFormatter.ofPattern(OutputView.DATE_FORMATTER, Locale.KOREAN))));
        }
    }

    private void recordAttendance() {
        Crew crew = getCrew();
        LocalTime checkInTime = getCheckInTime();
        LocalDateTime attendanceDateTime = LocalDateTime.of(SYSTEM_DATE, checkInTime);
        Attendance attendance = Attendance.of(attendanceDateTime);
        attendancesBook.addAttendance(crew, attendance);
        OutputView.printAttendanceResult(attendance);
    }

    private Crew getCrew() {
        String inputNickName = InputView.readNickName();
        return crews.getCrew(inputNickName);
    }

    private LocalTime getCheckInTime() {
        String inputCheckInTime = InputView.readCheckInTime();
        return LocalTime.parse(inputCheckInTime);
    }

    private void modifyAttendance() {
        String nickName = InputView.readModifyingNickName();
        Crew crew = crews.getCrew(nickName);
        LocalDate modifyingCheckInDate = getModifyingCheckInDate();
        validateAttendanceDate(modifyingCheckInDate);
        LocalTime modifyingCheckInTime = getModifyingCheckInTime();

        Attendance previousAttendance = attendancesBook.getExistAttendanceOfCrew(crew, modifyingCheckInDate);
        Attendance modifiedAttendance = attendancesBook.modify(crew, previousAttendance, modifyingCheckInTime);
        OutputView.printModifyingResult(previousAttendance, modifiedAttendance);
    }

    private LocalTime getModifyingCheckInTime() {
        String inputModifyingCheckinTime = InputView.readModifyingCheckinTime();
        return LocalTime.parse(inputModifyingCheckinTime);
    }

    private LocalDate getModifyingCheckInDate() {
        String inputModifyingCheckinDate = InputView.readModifyingCheckinDate();
        return LocalDate.of(SYSTEM_DATE.getYear(), SYSTEM_DATE.getMonth(), Integer.parseInt(inputModifyingCheckinDate));
    }

    private void checkAttendanceRecordOfCrew() {
        Crew crew = getCrew();
        List<Attendance> attendancesOfCrew = attendancesBook.getAttendancesOfCrew(crew, SYSTEM_DATE);
        int attendanceCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.CHECKIN);
        int lateCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.LATE);
        int absenceCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.ABSENCE);
        Penalty penalty = Penalty.determine(absenceCount, lateCount);
        OutputView.printAttendancesAndPenalty(attendancesOfCrew, crew, attendanceCount, lateCount, absenceCount, penalty);
    }
}
