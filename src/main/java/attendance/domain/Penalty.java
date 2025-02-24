package attendance.domain;

public enum Penalty {
    REMOVAL("제적"),
    INTERVIEW("면담"),
    WARNING("경고"),
    NONE(null)
    ;

    private static final int LATE_PER_ABSENCE = 3;
    private static final int WARNING_LIMIT = 2;
    private static final int INTERVIEW_LIMIT = 3;
    private static final int REMOVAL_LIMIT = 5;

    private final String status;

    Penalty(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Penalty determine(int absenceCount, int lateCount) {
        absenceCount = absenceCount + lateCount / LATE_PER_ABSENCE;
        if (absenceCount > REMOVAL_LIMIT) {
            return Penalty.REMOVAL;
        }
        if (absenceCount >= INTERVIEW_LIMIT) {
            return Penalty.INTERVIEW;
        }
        if (absenceCount == WARNING_LIMIT) {
            return Penalty.WARNING;
        }
        return Penalty.NONE;
    }

}
