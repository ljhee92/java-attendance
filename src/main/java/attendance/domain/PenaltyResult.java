package attendance.domain;

public record PenaltyResult(String nickName, int absenceCount, int lateCount, String penalty) {
}
