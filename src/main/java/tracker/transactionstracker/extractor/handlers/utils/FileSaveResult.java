package tracker.transactionstracker.extractor.handlers.utils;

public record FileSaveResult(boolean success, String fileName) {
    public boolean success() {
        return success;
    }

    @Override
    public String fileName() {
        return fileName;
    }
}
