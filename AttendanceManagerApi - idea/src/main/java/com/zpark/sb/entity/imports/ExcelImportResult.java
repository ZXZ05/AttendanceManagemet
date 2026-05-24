package com.zpark.sb.entity.imports;

import java.util.ArrayList;
import java.util.List;

public class ExcelImportResult {
    private int total;
    private int success;
    private int skipped;
    private final List<String> errors = new ArrayList<>();

    public void increaseTotal() {
        this.total++;
    }

    public void increaseSuccess() {
        this.success++;
    }

    public void increaseSkipped() {
        this.skipped++;
    }

    public void addError(String errorMessage) {
        if (errorMessage != null && !errorMessage.trim().isEmpty() && errors.size() < 20) {
            errors.add(errorMessage);
        }
    }

    public int getTotal() {
        return total;
    }

    public int getSuccess() {
        return success;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getFailed() {
        return Math.max(total - success - skipped, 0);
    }

    public List<String> getErrors() {
        return errors;
    }
}

