package attendance.view;

import attendance.domain.AttendanceResult;
import attendance.domain.Crew;
import attendance.domain.PenaltyResult;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class OutputView {

    public static final String DATE_FORMATTER = "MM월 dd일 EEE요일";
    private static final String TIME_FORMATTER = "HH:mm";
    private static final String ABSENCE_FORMATTER = "MM월 dd일 EEE요일 --:--";

    private OutputView() {
    }

    public static void printAttendanceResult(AttendanceResult attendanceResult) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            DATE_FORMATTER + " " + TIME_FORMATTER, Locale.KOREAN);
        String attendanceDate = attendanceResult.dateTime().format(dateTimeFormatter);
        System.out.printf("%n%s (%s)%n", attendanceDate, attendanceResult.status());
    }

    public static void printModifyingResult(AttendanceResult previousResult, AttendanceResult modifiedResult) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER, Locale.KOREAN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMATTER, Locale.KOREAN);

        String date = previousResult.dateTime().format(dateFormatter);
        String beforeTime = previousResult.dateTime().format(timeFormatter);
        String beforeStatus = previousResult.status();
        String afterTime = modifiedResult.dateTime().toLocalTime().format(timeFormatter);
        String afterStatus = modifiedResult.status();
        System.out.printf("%n%s %s (%s) -> %s (%s) 수정 완료!%n",
            date, beforeTime, beforeStatus, afterTime, afterStatus);
    }

    public static void printAttendancesAndPenalty(List<AttendanceResult> attendanceResults, String nickName,
                                                  int attendanceCount, int lateCount, int absenceCount, String penalty) {
        System.out.printf("%n이번 달 %s의 출석 기록입니다.%n", nickName);
        attendanceResults.forEach(attendanceResult -> System.out.println(getFormattedAttendanceRecord(attendanceResult)));

        System.out.printf("%n출석: %d회%n", attendanceCount);
        System.out.printf("지각: %d회%n", lateCount);
        System.out.printf("결석: %d회%n", absenceCount);

        if (penalty != null) {
            System.out.printf("%n%s 대상자입니다.%n", penalty);
        }
    }

    private static String getFormattedAttendanceRecord(AttendanceResult attendanceResult) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER + " " + TIME_FORMATTER, Locale.KOREAN);
        DateTimeFormatter absenceFormatter = DateTimeFormatter.ofPattern(ABSENCE_FORMATTER, Locale.KOREAN);

        String attendanceDateTime = attendanceResult.dateTime().format(dateTimeFormatter);
        String status = " (" + attendanceResult.status() + ")";
        if (attendanceResult.status().equals("결석")) {
            attendanceDateTime = attendanceResult.dateTime().format(absenceFormatter);
        }
        return attendanceDateTime + status;
    }

    public static void printPenaltyResults(List<PenaltyResult> penaltyResults) {
        System.out.println("제적 위험자 조회 결과");
        penaltyResults.sort(Comparator.comparing((PenaltyResult result) -> statusSort(result.penalty()))
                .thenComparing(PenaltyResult::nickName));

        printPenaltyResult(penaltyResults);
    }

    private static int statusSort(String status) {
        if ("제적".equals(status)) {
            return 0;
        }
        if ("면담".equals(status)) {
            return 1;
        }
        if ("경고".equals(status)) {
            return 2;
        }
        return 3;
    }

    private static void printPenaltyResult(List<PenaltyResult> penaltyResults) {
        penaltyResults.forEach(penaltyResult -> {
            if (penaltyResult.penalty() != null) {
                System.out.printf("- %s: 결석 %d회, 지각 %d회 (%s)", penaltyResult.nickName(),
                        penaltyResult.absenceCount(),
                        penaltyResult.lateCount(),
                        penaltyResult.penalty());
                System.out.println();
            }
        });
    }
}
