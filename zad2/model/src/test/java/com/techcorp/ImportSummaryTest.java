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
        assertNotNull(importSummary.getSummary());
        assertEquals(0, importSummary.getSummary().size());
    }

    @Test
    public void testAddSuccessfullImport()
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.addSuccessfullImport(new Employee("John", "Doe", "john.doe@example.com", "TechCorp", Role.ENGINEER, 10000));
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

