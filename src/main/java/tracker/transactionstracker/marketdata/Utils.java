package tracker.transactionstracker.marketdata;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Utils {

    private final static  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");
    private final static ZoneId UTC = ZoneId.of("UTC");
    public static String convertDateFromMilliSecond(long unixDate) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(unixDate),UTC);
        return date.format(DATE_TIME_FORMATTER);
    }
    public static String convertDateFromSecond(long unixDate) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixDate), UTC);
        return date.format(DATE_TIME_FORMATTER);
    }
}
