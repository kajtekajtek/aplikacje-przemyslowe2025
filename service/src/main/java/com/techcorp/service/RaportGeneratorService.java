package com.techcorp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.io.ByteArrayOutputStream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.techcorp.model.CompanyStatistics;
import com.techcorp.model.Employee;

@Service
public class RaportGeneratorService {
    private final EmployeeService employeeService;

    public RaportGeneratorService(
        EmployeeService employeeService
    ) {
        this.employeeService = employeeService;
    }

    public String generateCsvReport() {
        return generateCsvReport(employeeService.getEmployees());
    }

    public String generateCsvReport(String companyName) {
        return generateCsvReport(employeeService.getEmployeesByCompanyName(companyName));
    }

    private String generateCsvReport(List<Employee> employees) {
        StringBuilder csv = new StringBuilder();
        csv.append("firstName,lastName,email,company,position,salary,status\n");
        for (Employee employee : employees) {
            csv.append(employee.getFirstName()).append(",");
            csv.append(employee.getLastName()).append(",");
            csv.append(employee.getEmailAddress()).append(",");
            csv.append(employee.getCompanyName()).append(",");
            csv.append(employee.getRole()).append(",");
            csv.append(employee.getSalary()).append(",");
            csv.append(employee.getStatus()).append("\n");
        }
        return csv.toString();
    }

    public byte[] generatePdfReport(String companyName) {
        CompanyStatistics stats = employeeService.getCompanyStatistics(companyName);
        return generatePdfReport(stats);
    }

    private byte[] generatePdfReport(CompanyStatistics stats) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Title
            Paragraph title = new Paragraph("Company Statistics Report")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Company name
            Paragraph companyPara = new Paragraph("Company: " + stats.getCompanyName())
                .setFontSize(14)
                .setBold()
                .setMarginTop(20);
            document.add(companyPara);

            // Statistics table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginTop(10);

            // Add table headers
            table.addCell(new Cell().add(new Paragraph("Metric").setBold()));
            table.addCell(new Cell().add(new Paragraph("Value").setBold()));

            // Add statistics data
            table.addCell(new Cell().add(new Paragraph("Employee Count")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getEmployeesCount()))));

            table.addCell(new Cell().add(new Paragraph("Average Salary")));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", stats.getAverageSalary()))));

            table.addCell(new Cell().add(new Paragraph("Highest Salary")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getHighestSalary()))));

            table.addCell(new Cell().add(new Paragraph("Top Earner")));
            table.addCell(new Cell().add(new Paragraph(stats.getTopEarnerName())));

            document.add(table);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

}
