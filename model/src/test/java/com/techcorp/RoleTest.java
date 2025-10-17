package com.techcorp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest
{
    public final int CEO_LEVEL = 1;
    public final int VP_LEVEL = 2;
    public final int MANAGER_LEVEL = 3;
    public final int ENGINEER_LEVEL = 4;
    public final int INTERN_LEVEL = 5;
    public final int CEO_BASE_SALARY = 25000;
    public final int VP_BASE_SALARY = 18000;
    public final int MANAGER_BASE_SALARY = 12000;
    public final int ENGINEER_BASE_SALARY = 8000;
    public final int INTERN_BASE_SALARY = 3000;

    @Test
    public void testCEORole()
    {
        assertEquals(CEO_LEVEL, Role.CEO.getLevel());
        assertEquals(CEO_BASE_SALARY, Role.CEO.getBaseSalary());
    }

    @Test
    public void testVPRole()
    {
        assertEquals(VP_LEVEL, Role.VP.getLevel());
        assertEquals(VP_BASE_SALARY, Role.VP.getBaseSalary());
    }

    @Test
    public void testManagerRole()
    {
        assertEquals(MANAGER_LEVEL, Role.MANAGER.getLevel());
        assertEquals(MANAGER_BASE_SALARY, Role.MANAGER.getBaseSalary());
    }

    @Test
    public void testEngineerRole()
    {
        assertEquals(ENGINEER_LEVEL, Role.ENGINEER.getLevel());
        assertEquals(ENGINEER_BASE_SALARY, Role.ENGINEER.getBaseSalary());
    }

    @Test
    public void testInternRole()
    {
        assertEquals(INTERN_LEVEL, Role.INTERN.getLevel());
        assertEquals(INTERN_BASE_SALARY, Role.INTERN.getBaseSalary());
    }

    @Test
    public void testAllRolesExist()
    {
        Role[] roles = Role.values();
        assertEquals(5, roles.length);
        
        boolean hasCEO = false;
        boolean hasVP = false;
        boolean hasManager = false;
        boolean hasEngineer = false;
        boolean hasIntern = false;
        
        for (Role role : roles) {
            if (role == Role.CEO) hasCEO = true;
            if (role == Role.VP) hasVP = true;
            if (role == Role.MANAGER) hasManager = true;
            if (role == Role.ENGINEER) hasEngineer = true;
            if (role == Role.INTERN) hasIntern = true;
        }
        
        assertTrue(hasCEO, "CEO role should exist");
        assertTrue(hasVP, "VP role should exist");
        assertTrue(hasManager, "MANAGER role should exist");
        assertTrue(hasEngineer, "ENGINEER role should exist");
        assertTrue(hasIntern, "INTERN role should exist");
    }

    @Test
    public void testRoleHierarchy()
    {
        assertTrue(Role.CEO.getLevel() < Role.VP.getLevel());
        assertTrue(Role.VP.getLevel() < Role.MANAGER.getLevel());
        assertTrue(Role.MANAGER.getLevel() < Role.ENGINEER.getLevel());
        assertTrue(Role.ENGINEER.getLevel() < Role.INTERN.getLevel());
    }

    @Test
    public void testBaseSalaryDecreaseWithLevel()
    {
        assertTrue(Role.CEO.getBaseSalary() > Role.VP.getBaseSalary());
        assertTrue(Role.VP.getBaseSalary() > Role.MANAGER.getBaseSalary());
        assertTrue(Role.MANAGER.getBaseSalary() > Role.ENGINEER.getBaseSalary());
        assertTrue(Role.ENGINEER.getBaseSalary() > Role.INTERN.getBaseSalary());
    }

    @Test
    public void testValueOf()
    {
        assertEquals(Role.CEO, Role.valueOf("CEO"));
        assertEquals(Role.VP, Role.valueOf("VP"));
        assertEquals(Role.MANAGER, Role.valueOf("MANAGER"));
        assertEquals(Role.ENGINEER, Role.valueOf("ENGINEER"));
        assertEquals(Role.INTERN, Role.valueOf("INTERN"));
    }

    @Test
    public void testEnumToString()
    {
        assertEquals("CEO", Role.CEO.toString());
        assertEquals("VP", Role.VP.toString());
        assertEquals("MANAGER", Role.MANAGER.toString());
        assertEquals("ENGINEER", Role.ENGINEER.toString());
        assertEquals("INTERN", Role.INTERN.toString());
    }

    @Test
    public void testBaseSalariesArePositive()
    {
        for (Role role : Role.values()) {
            assertTrue(role.getBaseSalary() > 0, 
                      "Base salary should be positive for " + role);
        }
    }

    @Test
    public void testLevelsArePositive()
    {
        for (Role role : Role.values()) {
            assertTrue(role.getLevel() > 0, 
                      "Level should be positive for " + role);
        }
    }
}
