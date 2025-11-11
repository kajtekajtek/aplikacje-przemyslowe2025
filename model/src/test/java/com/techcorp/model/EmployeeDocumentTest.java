package com.techcorp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDocumentTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create EmployeeDocument with valid data")
        void shouldCreateEmployeeDocumentWithValidData() {
            EmployeeDocument document = new EmployeeDocument(
                "john.doe@techcorp.com",
                "document_123.pdf",
                "contract.pdf",
                DocumentType.CONTRACT,
                "/uploads/documents/john.doe@techcorp.com/document_123.pdf"
            );

            assertNotNull(document);
            assertNotNull(document.getId());
            assertEquals("john.doe@techcorp.com", document.getEmployeeEmail());
            assertEquals("document_123.pdf", document.getFileName());
            assertEquals("contract.pdf", document.getOriginalFileName());
            assertEquals(DocumentType.CONTRACT, document.getFileType());
            assertNotNull(document.getUploadDate());
            assertEquals("/uploads/documents/john.doe@techcorp.com/document_123.pdf", document.getFilePath());
        }

        @Test
        @DisplayName("Should generate unique ID for each document")
        void shouldGenerateUniqueIdForEachDocument() {
            EmployeeDocument doc1 = new EmployeeDocument(
                "john@techcorp.com", "file1.pdf", "original1.pdf",
                DocumentType.CONTRACT, "/path/file1.pdf"
            );
            EmployeeDocument doc2 = new EmployeeDocument(
                "john@techcorp.com", "file2.pdf", "original2.pdf",
                DocumentType.CONTRACT, "/path/file2.pdf"
            );

            assertNotEquals(doc1.getId(), doc2.getId());
        }

        @Test
        @DisplayName("Should convert email to lowercase")
        void shouldConvertEmailToLowercase() {
            EmployeeDocument document = new EmployeeDocument(
                "John.Doe@TechCorp.COM",
                "file.pdf", "original.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );

            assertEquals("john.doe@techcorp.com", document.getEmployeeEmail());
        }

        @Test
        @DisplayName("Should set upload date automatically")
        void shouldSetUploadDateAutomatically() {
            LocalDateTime before = LocalDateTime.now();
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );
            LocalDateTime after = LocalDateTime.now();

            assertTrue(document.getUploadDate().isAfter(before) || document.getUploadDate().isEqual(before));
            assertTrue(document.getUploadDate().isBefore(after) || document.getUploadDate().isEqual(after));
        }

        @Test
        @DisplayName("Should throw exception when employee email is null")
        void shouldThrowExceptionWhenEmployeeEmailIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument(null, "file.pdf", "original.pdf", DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when employee email is empty")
        void shouldThrowExceptionWhenEmployeeEmailIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("", "file.pdf", "original.pdf", DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when file name is null")
        void shouldThrowExceptionWhenFileNameIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", null, "original.pdf", DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("File name cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when file name is empty")
        void shouldThrowExceptionWhenFileNameIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "", "original.pdf", DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("File name cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when original file name is null")
        void shouldThrowExceptionWhenOriginalFileNameIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "file.pdf", null, DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("Original file name cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when original file name is empty")
        void shouldThrowExceptionWhenOriginalFileNameIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "file.pdf", "", DocumentType.CONTRACT, "/path")
            );
            assertTrue(exception.getMessage().contains("Original file name cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when file type is null")
        void shouldThrowExceptionWhenFileTypeIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "file.pdf", "original.pdf", null, "/path")
            );
            assertTrue(exception.getMessage().contains("File type cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when file path is null")
        void shouldThrowExceptionWhenFilePathIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "file.pdf", "original.pdf", DocumentType.CONTRACT, null)
            );
            assertTrue(exception.getMessage().contains("File path cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when file path is empty")
        void shouldThrowExceptionWhenFilePathIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("john@techcorp.com", "file.pdf", "original.pdf", DocumentType.CONTRACT, "")
            );
            assertTrue(exception.getMessage().contains("File path cannot be null or empty"));
        }
    }

    @Nested
    @DisplayName("Document Type Tests")
    class DocumentTypeTest {

        @Test
        @DisplayName("Should create document with CONTRACT type")
        void shouldCreateDocumentWithContractType() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "contract.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );

            assertEquals(DocumentType.CONTRACT, document.getFileType());
        }

        @Test
        @DisplayName("Should create document with CERTIFICATE type")
        void shouldCreateDocumentWithCertificateType() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "cert.pdf",
                DocumentType.CERTIFICATE, "/path/file.pdf"
            );

            assertEquals(DocumentType.CERTIFICATE, document.getFileType());
        }

        @Test
        @DisplayName("Should create document with ID_CARD type")
        void shouldCreateDocumentWithIdCardType() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "id.pdf",
                DocumentType.ID_CARD, "/path/file.pdf"
            );

            assertEquals(DocumentType.ID_CARD, document.getFileType());
        }

        @Test
        @DisplayName("Should create document with OTHER type")
        void shouldCreateDocumentWithOtherType() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "other.pdf",
                DocumentType.OTHER, "/path/file.pdf"
            );

            assertEquals(DocumentType.OTHER, document.getFileType());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("Should be equal when IDs are the same")
        void shouldBeEqualWhenIdsAreTheSame() {
            LocalDateTime now = LocalDateTime.now();
            EmployeeDocument doc1 = new EmployeeDocument(
                "doc-123", "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, now, "/path/file.pdf"
            );
            EmployeeDocument doc2 = new EmployeeDocument(
                "doc-123", "jane@techcorp.com", "other.pdf", "different.pdf",
                DocumentType.CERTIFICATE, now, "/path/other.pdf"
            );

            assertEquals(doc1, doc2);
            assertEquals(doc1.hashCode(), doc2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            LocalDateTime now = LocalDateTime.now();
            EmployeeDocument doc1 = new EmployeeDocument(
                "doc-123", "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, now, "/path/file.pdf"
            );
            EmployeeDocument doc2 = new EmployeeDocument(
                "doc-456", "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, now, "/path/file.pdf"
            );

            assertNotEquals(doc1, doc2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );

            assertEquals(document, document);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );

            assertNotEquals(document, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            EmployeeDocument document = new EmployeeDocument(
                "john@techcorp.com", "file.pdf", "original.pdf",
                DocumentType.CONTRACT, "/path/file.pdf"
            );

            assertNotEquals(document, "some string");
        }
    }

    @Nested
    @DisplayName("Full Constructor Tests")
    class FullConstructorTest {

        @Test
        @DisplayName("Should create document with all parameters")
        void shouldCreateDocumentWithAllParameters() {
            LocalDateTime uploadTime = LocalDateTime.of(2025, 11, 11, 10, 30);
            EmployeeDocument document = new EmployeeDocument(
                "doc-123",
                "john@techcorp.com",
                "file.pdf",
                "contract.pdf",
                DocumentType.CONTRACT,
                uploadTime,
                "/uploads/file.pdf"
            );

            assertEquals("doc-123", document.getId());
            assertEquals("john@techcorp.com", document.getEmployeeEmail());
            assertEquals("file.pdf", document.getFileName());
            assertEquals("contract.pdf", document.getOriginalFileName());
            assertEquals(DocumentType.CONTRACT, document.getFileType());
            assertEquals(uploadTime, document.getUploadDate());
            assertEquals("/uploads/file.pdf", document.getFilePath());
        }

        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            LocalDateTime now = LocalDateTime.now();
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument(null, "john@techcorp.com", "file.pdf", "original.pdf",
                    DocumentType.CONTRACT, now, "/path")
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when ID is empty")
        void shouldThrowExceptionWhenIdIsEmpty() {
            LocalDateTime now = LocalDateTime.now();
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("", "john@techcorp.com", "file.pdf", "original.pdf",
                    DocumentType.CONTRACT, now, "/path")
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when upload date is null")
        void shouldThrowExceptionWhenUploadDateIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeDocument("doc-123", "john@techcorp.com", "file.pdf", "original.pdf",
                    DocumentType.CONTRACT, null, "/path")
            );
            assertTrue(exception.getMessage().contains("Upload date cannot be null"));
        }
    }

    @Test
    @DisplayName("Should return correct string representation")
    void shouldReturnCorrectStringRepresentation() {
        LocalDateTime uploadTime = LocalDateTime.of(2025, 11, 11, 10, 30);
        EmployeeDocument document = new EmployeeDocument(
            "doc-123",
            "john@techcorp.com",
            "file.pdf",
            "contract.pdf",
            DocumentType.CONTRACT,
            uploadTime,
            "/path/file.pdf"
        );

        String toString = document.toString();
        assertTrue(toString.contains("doc-123"));
        assertTrue(toString.contains("john@techcorp.com"));
        assertTrue(toString.contains("file.pdf"));
        assertTrue(toString.contains("CONTRACT"));
    }
}

