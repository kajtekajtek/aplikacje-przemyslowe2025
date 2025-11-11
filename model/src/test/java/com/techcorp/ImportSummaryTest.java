package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.techcorp.model.ImportSummary;
import com.techcorp.model.exception.InvalidDataException;

import java.io.FileNotFoundException;

public class ImportSummaryTest
{
    @Test
    public void testConstructor()
    {
        ImportSummary importSummary = new ImportSummary();

        assertNotNull(importSummary);
        assertEquals(0, importSummary.getSuccessCount());
        assertEquals(0, importSummary.getErrors().size());
    }

    @Test
    public void testAddSuccessfullImport()
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.addSuccessfullImport();
        assertEquals(1, importSummary.getSuccessCount());
    }

    @Test
    public void testAddInvalidDataExceptionError()
    {
        ImportSummary importSummary = new ImportSummary();
        InvalidDataException exception = new InvalidDataException(1, "Error message");

        importSummary.addError(1, exception);

        assertEquals(1, importSummary.getErrors().size());
        assertEquals(InvalidDataException.class, importSummary.getErrors().get(1).getClass());
        assertEquals(1, ((InvalidDataException) importSummary.getErrors().get(1)).getLine());
        assertEquals("Error message", importSummary.getErrors().get(1).getMessage());
    }

    @Test
    public void testAddFileNotFoundExceptionError()
    {
        ImportSummary importSummary = new ImportSummary();
        FileNotFoundException exception = new FileNotFoundException("Error message");

        importSummary.addError(1, exception);

        assertEquals(1, importSummary.getErrors().size());
        assertEquals(FileNotFoundException.class, importSummary.getErrors().get(1).getClass());
        assertEquals("Error message", importSummary.getErrors().get(1).getMessage());
    }
}
