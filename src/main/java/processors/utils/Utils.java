package processors.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Utils {
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    public static final String noDataFound = "No data found: ";
    public static final String FILE_PATH = "./download/";
    public static String fileName = "";


    public static String getId(String[] line, String chain) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("M-dd-yyyy");
        LocalDate date = LocalDate.parse(line[0], inputFormat);
        String formattedDate = date.format(outputFormat);
        return chain + "-" + formattedDate;
    }

    public static String getChain(String chain) {
        return chain + "-";
    }


    public static long convertDateToUnix(String date) {
        LocalDate localDate = LocalDate.parse(date);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID);
        return zonedDateTime.toEpochSecond();
    }

    public static long convertDateToUnixSpecialFormat(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID);
        return zonedDateTime.toEpochSecond();
    }

    public static long convertDateToUnixSpecialFormatTwo(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("M-dd-yyyy"));
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID);
        return zonedDateTime.toEpochSecond();
    }

    public static long convertDateToUnixForNearAndCosmos(String date) {
        Instant instant = Instant.parse(date);
        return instant.getEpochSecond();
    }

    public static String convertUnixSecondToNormalDate(long unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyy");
        return localDateTime.format(formatter);
    }

    public static String convertUnixMilisecondToNormalDate(long unixTime) {
        Instant instant = Instant.ofEpochMilli(unixTime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyy");
        return localDateTime.format(formatter);
    }

    public static boolean downloadCSVFromWebsite(String url) {
        AtomicBoolean result = new AtomicBoolean(false);
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

            Page page = browser.newPage();
            page.navigate(url);

            // Wait for the page to load
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForSelector("//span[contains(text(), 'CSV Data')]");


            saveFileFromPath(page, FILE_PATH, "").thenAccept(fileSaveResult -> {
                result.set(fileSaveResult.success());
                fileName = fileSaveResult.fileName();

            });

            ElementHandle downloadButton = page.querySelector("//span[contains(text(), 'CSV Data')]");
            if (downloadButton != null) {
                downloadButton.click();
            } else {
                log.info("Download button not found");
            }

            if (!sleepThread(3000))
                log.info("Download interrupted");


            return result.get();
        } catch (Exception e) {
            log.info("Error downloading file", e);
            return false;
        }
    }

    public static CompletableFuture<FileSaveResult> saveFileFromPath(Page page, String path, String filename) {
        CompletableFuture<FileSaveResult> future = new CompletableFuture<>();

        page.onDownload(download -> {
            try {
                String finalFileName = (filename == null || filename.isEmpty()) ? download.suggestedFilename() : filename;
                download.saveAs(Paths.get(path + finalFileName));
                future.complete(new FileSaveResult(true, finalFileName));
            } catch (Exception e) {
                log.info("Error saving file", e);
                future.complete(new FileSaveResult(false, ""));
            }
        });
        return future;
    }

    private static boolean sleepThread(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static LocalDateTime getPreviousDate() {
        LocalDate localDate = LocalDate.now(ZONE_ID);
        return localDate.atStartOfDay().minusDays(1);
    }

    public static String getPreviousDateToString() {
        LocalDate localDate = LocalDate.from(LocalDate.now(ZONE_ID).atStartOfDay());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");
        return localDate.minusDays(1).format(formatter);
    }
}
