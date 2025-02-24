package attendance.domain;

import attendance.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Attendances {

    private final List<Attendance> attendances;

    public Attendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public void add(Attendance attendance) {
        validateAlreadyAttended(attendance);
        attendances.add(attendance);
    }

    private void validateAlreadyAttended(Attendance attendance) {
        attendances.stream()
                .filter(existAttendance -> existAttendance.getAttendanceDateTime().getDayOfMonth()
                        == attendance.getAttendanceDateTime().getDayOfMonth())
                .findFirst()
                .ifPresent(exception -> {
                    throw new IllegalArgumentException("\n[ERROR] 이미 출석을 완료했습니다. 수정 기능을 이용해주세요.");
                });
    }

    public Attendance get(LocalDate modifyingDate) {
        return attendances.stream()
                .filter(attendance -> attendance.getAttendanceDateTime().getDayOfMonth() == modifyingDate.getDayOfMonth())
                .findFirst()
                .orElse(Attendance.of(LocalDateTime.of(modifyingDate, LocalTime.MIN)));
    }

    public Attendance modify(Attendance existAttendance, LocalTime modifyingTime) {
        LocalDate modifyingDate = existAttendance.getAttendanceDateTime().toLocalDate();
        attendances.remove(existAttendance);
        return attendances.stream()
                .filter(attendance -> attendance.getAttendanceDateTime().toLocalDate() == modifyingDate)
                .peek(attendance -> attendance.modify(LocalDateTime.of(modifyingDate, modifyingTime)))
                .findFirst()
                .orElseGet(() -> {
                    Attendance newAttendance = Attendance.of(LocalDateTime.of(modifyingDate, modifyingTime));
                    attendances.sort(Comparator.comparing(Attendance::getAttendanceDateTime));
                    int insertIndex = findInsertIndex(newAttendance);
                    attendances.add(insertIndex, newAttendance);
                    return newAttendance;
                });
    }

    private int findInsertIndex(Attendance newAttendance) {
        for (int i = 0; i < attendances.size(); i++) {
            if (attendances.get(i).getAttendanceDateTime().isAfter(newAttendance.getAttendanceDateTime())) {
                return i;
            }
        }
        return attendances.size();
    }

    public List<Attendance> getAddAbsenceAttendances(LocalDate today) {
        int day = today.getDayOfMonth();
        List<Attendance> newAttendances = new ArrayList<>();
        for (int i = 1; i < day; i++) {
            addAttendance(today, i, newAttendances);
        }
        return newAttendances;
    }

    private void addAttendance(LocalDate today, int i, List<Attendance> newAttendances) {
        LocalDate currentDay = LocalDate.of(today.getYear(), today.getMonth(), i);
        if (DateUtil.isWeekend(currentDay)) {
            return;
        }
        Attendance attendance = attendances.stream()
                .filter(existAttendance -> existAttendance.getAttendanceDateTime().toLocalDate().equals(currentDay))
                .findFirst()
                .orElse(Attendance.of(LocalDateTime.of(currentDay, LocalTime.MIN)));
        newAttendances.add(attendance);
    }
}
