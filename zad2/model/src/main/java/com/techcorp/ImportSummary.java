package com.techcorp;

import java.util.Map;
import java.util.HashMap;

public class ImportSummary
{
    private int successCount;
    private Map<Integer, String> errors;

    public ImportSummary() {
        this.successCount = 0;
        this.errors = new HashMap<>();
    }

    public void addSuccessfullImport() {
        this.successCount++;
    }

    public void addError(int line, String error) {
        this.errors.put(line, error);
    }
    
    public int getSuccessCount() {
        return this.successCount;
    }

    public Map<Integer, String> getErrors() {
        return this.errors;
    }
}