package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImportSummaryTest
{
    /* 
    Obiekt ImportSummary zawiera listę zaimportowanych pracowników oraz
    listę błędów zawierających informację o błędnych wierszach i opis błędu
    */

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
    public void testAddError()
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.addError(1, "Error message");
        assertEquals(1, importSummary.getErrors().size());
        assertEquals("Error message", importSummary.getErrors().get(1));
    }
    
}

