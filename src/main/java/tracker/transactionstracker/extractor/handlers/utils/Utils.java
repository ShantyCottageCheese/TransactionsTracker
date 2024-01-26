package tracker.transactionstracker.extractor.handlers.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Utils {
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    public static final String NO_DATA_FOUND = "No data found: ";
    public static final String FILE_PATH = "./src/main/java/tracker/transactionstracker/extractor/temp/";
    public static String fileName = "unknown";

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

    public static long convertDateToUnixFromYMD(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID);
        return zonedDateTime.toEpochSecond();
    }

    public static long convertDateToUnixFromMDY(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("M-dd-yyyy"));
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID);
        return zonedDateTime.toEpochSecond();
    }

    public static String convertUnixSecondToDate(long unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyy");
        return localDateTime.format(formatter);
    }


    public static boolean downloadCSVFromWebsite(String url) {
        AtomicBoolean result = new AtomicBoolean(false);
        try (Playwright playwright = Playwright.create()) {
            Page page;
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
                page = browser.newPage();

            page.navigate(url);

            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForSelector("//span[contains(text(), 'CSV Data')]");

            CompletableFuture<FileSaveResult> fileSaveFuture = saveFileFromPath(page, FILE_PATH, "");

            ElementHandle downloadButton = page.querySelector("//span[contains(text(), 'CSV Data')]");
            if (downloadButton != null) {
                downloadButton.click();
                FileSaveResult fileSaveResult = fileSaveFuture.get(); // Czekaj na zakończenie pobierania
                result.set(fileSaveResult.success());
                fileName = fileSaveResult.fileName();
            } else {
                log.info("Download button not found");
            }
        } catch (Exception e) {
            log.info("Error downloading file", e);
            return false;
        }
        return result.get();
    }

    public static CompletableFuture<FileSaveResult> saveFileFromPath(Page page, String path, String filename) {
        CompletableFuture<FileSaveResult> future = new CompletableFuture<>();
        page.onDownload(download -> {
            try {
                String finalFileName = filename.isEmpty() ? download.suggestedFilename() : filename;
                Path filePath = Paths.get(path, finalFileName);
                download.saveAs(filePath);
                future.complete(new FileSaveResult(true, finalFileName));
            } catch (Exception e) {
                log.info("Error saving file", e);
                future.complete(new FileSaveResult(false, ""));
            }
        });
        return future;
    }


    public static String getPreviousDate() {
        LocalDate localDate = LocalDate.from(LocalDate.now(ZONE_ID).atStartOfDay());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");
        return localDate.minusDays(1).format(formatter);
    }
}
