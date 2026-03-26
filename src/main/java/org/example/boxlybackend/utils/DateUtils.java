package org.example.boxlybackend.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public class DateUtils {

    public static int countSubscribedDaysInCurrentMonth(Set<DayOfWeek> subscribedDays) {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now);

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        int count = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (subscribedDays.contains(date.getDayOfWeek())) {
                count++;
            }
        }

        return count;
    }

    public static int countWorkDaysInCurrentMonth() {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now);
        List<DayOfWeek> weekends = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        int count = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (!weekends.contains(date.getDayOfWeek())) {
                count++;
            }
        }

        return count;
    }
}
