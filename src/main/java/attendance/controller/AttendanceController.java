package attendance.controller;

import attendance.constant.Holiday;
import attendance.domain.Attendance;
import attendance.domain.AttendanceResult;
import attendance.domain.AttendanceStatus;
import attendance.domain.AttendancesBook;
import attendance.domain.Crew;
import attendance.domain.Crews;
import attendance.domain.Penalty;
import attendance.domain.PenaltyResult;
import attendance.file.AttendanceFileReader;
import attendance.util.DateUtil;
import attendance.view.InputView;
import attendance.view.OutputView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        repeat();
    }

    private void repeat() {
        while (true) {
            String inputFunction = InputView.readFunction();
            if ("Q".equals(inputFunction)) {
                break;
            }
            try {
                performFunction(inputFunction);
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void performFunction(String inputFunction) {
        if ("1".equals(inputFunction)) {
            validateAttendanceDate(SYSTEM_DATE);
            recordAttendance();
        }
        if ("2".equals(inputFunction)) {
            modifyAttendance();
        }
        if ("3".equals(inputFunction)) {
            checkAttendanceRecordOfCrew();
        }
        if ("4".equals(inputFunction)) {
            printPenaltyResult();
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
                    DateTimeFormatter.ofPattern(DateUtil.DATE_FORMATTER, Locale.KOREAN))));
        }
    }

    private void recordAttendance() {
        Crew crew = getCrew();
        LocalTime checkInTime = getCheckInTime();
        LocalDateTime attendanceDateTime = LocalDateTime.of(SYSTEM_DATE, checkInTime);
        Attendance attendance = Attendance.of(attendanceDateTime);
        attendancesBook.addAttendance(crew, attendance);
        OutputView.printAttendanceResult(new AttendanceResult(attendance.getAttendanceDateTime(), attendance.getStatus().getName()));
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
        AttendanceResult previousResult = new AttendanceResult(previousAttendance.getAttendanceDateTime(), previousAttendance.getStatus().getName());
        AttendanceResult modifiedResult = new AttendanceResult(modifiedAttendance.getAttendanceDateTime(), modifiedAttendance.getStatus().getName());
        OutputView.printModifyingResult(previousResult, modifiedResult);
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
        List<AttendanceResult> attendanceResults = attendancesOfCrew.stream()
                .map(attendance -> new AttendanceResult(attendance.getAttendanceDateTime(), attendance.getStatus().getName()))
                .toList();

        int attendanceCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.CHECKIN);
        int lateCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.LATE);
        int absenceCount = attendancesBook.countAttendanceStatus(attendancesOfCrew, AttendanceStatus.ABSENCE);
        String penalty = Penalty.determine(absenceCount, lateCount).getStatus();
        OutputView.printAttendancesAndPenalty(attendanceResults, crew.getNickName(), attendanceCount, lateCount, absenceCount, penalty);
    }

    private void printPenaltyResult() {
        List<PenaltyResult> penaltyResults = crews.getCrews().stream()
                .map(crew -> {
                    List<Attendance> attendanceOfCrew = attendancesBook.getAttendancesOfCrew(crew, LocalDate.now());
                    int absenceCount = attendancesBook.countAttendanceStatus(attendanceOfCrew, AttendanceStatus.ABSENCE);
                    int lateCount = attendancesBook.countAttendanceStatus(attendanceOfCrew, AttendanceStatus.LATE);
                    Penalty penalty = Penalty.determine(absenceCount, lateCount);
                    return new PenaltyResult(crew.getNickName(), absenceCount, lateCount, penalty.getStatus());
                })
                .toList();
        OutputView.printPenaltyResults(new ArrayList<>(penaltyResults));
    }
}
