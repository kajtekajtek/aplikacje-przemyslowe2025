package com.techcorp.model;

import java.util.Objects;

public class Employee 
{
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String companyName;
    private Role   role;
    private int    salary;
	private EmploymentStatus status;
	private String photoFileName;

    public static Employee createEmployee(
        String lastName, 
        String firstName, 
        String emailAddress, 
        String companyName,
        Role   role
    ) {
		return new Employee(
			lastName, firstName, emailAddress, companyName, role, role.getBaseSalary(), EmploymentStatus.ACTIVE
		);
    }
    
    public Employee(
        String lastName, 
        String firstName, 
        String emailAddress, 
        String companyName, 
        Role   role, 
        int    salary
    ) {
		this(
			lastName,
			firstName,
			emailAddress,
			companyName,
			role,
			salary,
			EmploymentStatus.ACTIVE
		);
    }

	public Employee(
		String lastName,
		String firstName,
		String emailAddress,
		String companyName,
		Role   role,
		int    salary,
		EmploymentStatus status
	) {
		this(lastName, firstName, emailAddress, companyName, role, salary, status, null);
	}

	public Employee(
		String lastName,
		String firstName,
		String emailAddress,
		String companyName,
		Role   role,
		int    salary,
		EmploymentStatus status,
		String photoFileName
	) {
		validateParameters(lastName, firstName, emailAddress, companyName, role, salary, status);

		this.lastName     = lastName;
		this.firstName    = firstName;
		this.emailAddress = emailAddress.toLowerCase();
		this.companyName  = companyName;
		this.role         = role;
		this.salary       = salary;
		this.status       = status;
		this.photoFileName = photoFileName;
	}

	private void validateParameters(
        String lastName, 
        String firstName, 
        String emailAddress, 
        String companyName, 
        Role   role, 
        int    salary,
        EmploymentStatus status
    ) {
        if (salary < 0) 
            throw new IllegalArgumentException("Salary cannot be negative");
        if (lastName == null || lastName.isEmpty()) 
            throw new IllegalArgumentException("Last name cannot be empty");
        if (firstName == null || firstName.isEmpty()) 
            throw new IllegalArgumentException("First name cannot be empty");
        if (emailAddress == null || emailAddress.isEmpty()) 
            throw new IllegalArgumentException("Email address cannot be empty");
        if (companyName == null || companyName.isEmpty()) 
            throw new IllegalArgumentException("Company name cannot be empty");
        if (role == null) 
            throw new IllegalArgumentException("Role cannot be null");
        if (status == null) 
            throw new IllegalArgumentException("Employment status cannot be null");
    }

    public String getFullName()     { return this.firstName + " " + this.lastName; }
    public String getLastName()     { return this.lastName; }
    public String getFirstName()    { return this.firstName; }
    public String getEmailAddress() { return this.emailAddress; }
    public String getCompanyName()  { return this.companyName; }
    public Role   getRole()         { return this.role; }
    public int    getSalary()       { return this.salary; }
	public EmploymentStatus getStatus() { return this.status; }
	public String getPhotoFileName() { return this.photoFileName; }

    public void setSalary(int salary) { this.salary = salary; }
	public void setStatus(EmploymentStatus status) {
		if (status == null) throw new IllegalArgumentException("Employment status cannot be null");
		this.status = status;
	}
	public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }

	@Override
	public String toString() {
		return getFullName()
		     + " "
		     + getRole().toString()
		     + " "
		     + getEmailAddress()
		     + " "
		     + getStatus().toString();
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Employee employee = (Employee) obj;

        return Objects.equals(emailAddress, employee.emailAddress);
    }

    @Override
    public int hashCode() { return Objects.hash(emailAddress); }

}
