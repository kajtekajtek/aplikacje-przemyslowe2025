package com.techcorp;

import com.google.gson.JsonObject;

public class EmployeeMapper {
    
    public static Employee jsonObjectToEmployee(JsonObject jsonObject) {
        String fullName = jsonObject.get("name").getAsString();
        String[] fullNameSplit = parseFullName(fullName);
        String firstName = fullNameSplit[0];
        String lastName  = fullNameSplit[1];

        String email = jsonObject.get("email").getAsString();

        String companyName = jsonObject.getAsJsonObject("company")
                                       .get("name")
                                       .getAsString();

        Employee employee = Employee.createEmployee(
                lastName,
                firstName,
                email,
                companyName,
                Role.ENGINEER
        );

        return employee;
    }

    private static String[] parseFullName(String fullName) {
        String[] fullNameSplit = fullName.trim().split("\\s+", 2);
        String firstName = fullNameSplit.length > 0 
            ? fullNameSplit[0] 
            : "";
        String lastName = fullNameSplit.length > 1 
            ? fullNameSplit[1] 
            : "";

        return new String[] { firstName, lastName };
    }
}
