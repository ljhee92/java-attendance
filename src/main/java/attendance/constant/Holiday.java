package attendance.constant;

import java.time.LocalDate;
import java.util.Arrays;

public enum Holiday {
    NEW_YEAR(LocalDate.of(2025, 1, 1)),
    TEMPORARY_HOLIDAY(LocalDate.of(2025, 1, 27)),
    LUNAR_NEW_YEAR_HOLIDAY1(LocalDate.of(2025, 1, 28)),
    LUNAR_NEW_YEAR(LocalDate.of(2025, 1, 29)),
    LUNAR_NEW_YEAR_HOLIDAY2(LocalDate.of(2025, 1, 30)),
    INDEPENDENCE_DAY(LocalDate.of(2025, 3, 1)),
    SUBSTITUTE_INDEPENDENCE_DAY(LocalDate.of(2025, 3, 3)),
    CHILDREN_DAY(LocalDate.of(2025, 5, 5)),
    BUDDHA_BIRTHDAY(LocalDate.of(2025, 5, 5)),
    SUBSTITUTE_BUDDHA_BIRTHDAY(LocalDate.of(2025, 5, 6)),
    MEMORIAL_DAY(LocalDate.of(2025, 6, 6)),
    LIBERATION_DAY(LocalDate.of(2025, 8, 15)),
    NATIONAL_FOUNDATION_DAY(LocalDate.of(2025, 10, 3)),
    CHUSEOK_HOLIDAY1(LocalDate.of(2025, 10, 5)),
    CHUSEOK(LocalDate.of(2025, 10, 6)),
    CHUSEOK_HOLIDAY2(LocalDate.of(2025, 10, 7)),
    SUBSTITUTE_CHUSEOK(LocalDate.of(2025, 10, 8)),
    HANGUL_DAY(LocalDate.of(2025, 10, 9)),
    CHRISTMAS(LocalDate.of(2025, 12, 25))
    ;

    private final LocalDate date;

    Holiday(LocalDate date) {
        this.date = date;
    }

    public static boolean isHoliday(LocalDate date) {
        return Arrays.stream(values())
                .anyMatch(value -> value.date.equals(date));
    }
}
