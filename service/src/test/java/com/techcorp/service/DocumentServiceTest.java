package com.techcorp.service;

import com.techcorp.model.DocumentType;
import com.techcorp.model.EmployeeDocument;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    private DocumentService documentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(fileStorageService);
    }

    @AfterEach
    void tearDown() {
        documentService.clearAll();
    }

    @Nested
    @DisplayName("Save Document Tests")
    class SaveDocumentTest {

        @Test
        @DisplayName("Should save document with valid data")
        void shouldSaveDocumentWithValidData() {
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "contract.pdf",
                "application/pdf",
                "contract content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_contract.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            assertNotNull(document);
            assertNotNull(document.getId());
            assertEquals("john@techcorp.com", document.getEmployeeEmail());
            assertEquals("contract.pdf", document.getOriginalFileName());
            assertEquals(DocumentType.CONTRACT, document.getFileType());
            assertNotNull(document.getUploadDate());
            assertTrue(document.getFileName().endsWith("contract.pdf"));
            
            verify(fileStorageService, times(1)).saveFile(any(), eq("uploads/documents/john@techcorp.com"));
        }

        @Test
        @DisplayName("Should save multiple documents for same employee")
        void shouldSaveMultipleDocumentsForSameEmployee() {
            MockMultipartFile file1 = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "content1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "file", "certificate.pdf", "application/pdf", "content2".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid1_contract.pdf")
                .thenReturn("/uploads/documents/john@techcorp.com/uuid2_certificate.pdf");

            EmployeeDocument doc1 = documentService.saveDocument(
                "john@techcorp.com", file1, DocumentType.CONTRACT
            );
            EmployeeDocument doc2 = documentService.saveDocument(
                "john@techcorp.com", file2, DocumentType.CERTIFICATE
            );

            assertNotEquals(doc1.getId(), doc2.getId());
            assertEquals("john@techcorp.com", doc1.getEmployeeEmail());
            assertEquals("john@techcorp.com", doc2.getEmployeeEmail());

            List<EmployeeDocument> documents = documentService.getDocuments("john@techcorp.com");
            assertEquals(2, documents.size());
            
            verify(fileStorageService, times(2)).saveFile(any(), eq("uploads/documents/john@techcorp.com"));
        }

        @Test
        @DisplayName("Should normalize email to lowercase when saving")
        void shouldNormalizeEmailToLowercaseWhenSaving() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john.doe@techcorp.com/uuid_contract.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "John.Doe@TechCorp.COM", file, DocumentType.CONTRACT
            );

            assertEquals("john.doe@techcorp.com", document.getEmployeeEmail());
            verify(fileStorageService, times(1)).saveFile(any(), eq("uploads/documents/john.doe@techcorp.com"));
        }

        @Test
        @DisplayName("Should save documents for different employees")
        void shouldSaveDocumentsForDifferentEmployees() {
            MockMultipartFile file1 = new MockMultipartFile(
                "file", "doc1.pdf", "application/pdf", "content1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "file", "doc2.pdf", "application/pdf", "content2".getBytes()
            );

            when(fileStorageService.saveFile(any(), eq("uploads/documents/john@techcorp.com")))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc1.pdf");
            when(fileStorageService.saveFile(any(), eq("uploads/documents/jane@techcorp.com")))
                .thenReturn("/uploads/documents/jane@techcorp.com/uuid_doc2.pdf");

            documentService.saveDocument("john@techcorp.com", file1, DocumentType.CONTRACT);
            documentService.saveDocument("jane@techcorp.com", file2, DocumentType.CERTIFICATE);

            List<EmployeeDocument> johnDocs = documentService.getDocuments("john@techcorp.com");
            List<EmployeeDocument> janeDocs = documentService.getDocuments("jane@techcorp.com");

            assertEquals(1, johnDocs.size());
            assertEquals(1, janeDocs.size());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveDocument(null, file, DocumentType.CONTRACT)
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveDocument("", file, DocumentType.CONTRACT)
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when file is null")
        void shouldThrowExceptionWhenFileIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveDocument("john@techcorp.com", null, DocumentType.CONTRACT)
            );
            assertTrue(exception.getMessage().contains("File cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when file is empty")
        void shouldThrowExceptionWhenFileIsEmpty() {
            MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[0]
            );

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveDocument("john@techcorp.com", emptyFile, DocumentType.CONTRACT)
            );
            assertTrue(exception.getMessage().contains("File cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when document type is null")
        void shouldThrowExceptionWhenDocumentTypeIsNull() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveDocument("john@techcorp.com", file, null)
            );
            assertTrue(exception.getMessage().contains("Document type cannot be null"));
        }

        @Test
        @DisplayName("Should save different document types")
        void shouldSaveDifferentDocumentTypes() {
            MockMultipartFile contract = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "content".getBytes()
            );
            MockMultipartFile certificate = new MockMultipartFile(
                "file", "cert.pdf", "application/pdf", "content".getBytes()
            );
            MockMultipartFile idCard = new MockMultipartFile(
                "file", "id.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid1_contract.pdf")
                .thenReturn("/uploads/documents/john@techcorp.com/uuid2_cert.pdf")
                .thenReturn("/uploads/documents/john@techcorp.com/uuid3_id.pdf");

            EmployeeDocument doc1 = documentService.saveDocument("john@techcorp.com", contract, DocumentType.CONTRACT);
            EmployeeDocument doc2 = documentService.saveDocument("john@techcorp.com", certificate, DocumentType.CERTIFICATE);
            EmployeeDocument doc3 = documentService.saveDocument("john@techcorp.com", idCard, DocumentType.ID_CARD);

            assertEquals(DocumentType.CONTRACT, doc1.getFileType());
            assertEquals(DocumentType.CERTIFICATE, doc2.getFileType());
            assertEquals(DocumentType.ID_CARD, doc3.getFileType());
        }
    }

    @Nested
    @DisplayName("Get Documents Tests")
    class GetDocumentsTest {

        @Test
        @DisplayName("Should return empty list for employee with no documents")
        void shouldReturnEmptyListForEmployeeWithNoDocuments() {
            List<EmployeeDocument> documents = documentService.getDocuments("john@techcorp.com");

            assertNotNull(documents);
            assertTrue(documents.isEmpty());
        }

        @Test
        @DisplayName("Should return all documents for employee")
        void shouldReturnAllDocumentsForEmployee() {
            MockMultipartFile file1 = new MockMultipartFile(
                "file", "doc1.pdf", "application/pdf", "content1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "file", "doc2.pdf", "application/pdf", "content2".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid1_doc1.pdf")
                .thenReturn("/uploads/documents/john@techcorp.com/uuid2_doc2.pdf");

            documentService.saveDocument("john@techcorp.com", file1, DocumentType.CONTRACT);
            documentService.saveDocument("john@techcorp.com", file2, DocumentType.CERTIFICATE);

            List<EmployeeDocument> documents = documentService.getDocuments("john@techcorp.com");

            assertEquals(2, documents.size());
        }

        @Test
        @DisplayName("Should normalize email when getting documents")
        void shouldNormalizeEmailWhenGettingDocuments() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc.pdf");

            documentService.saveDocument("john@techcorp.com", file, DocumentType.CONTRACT);

            List<EmployeeDocument> documents = documentService.getDocuments("John@TechCorp.COM");

            assertEquals(1, documents.size());
        }

        @Test
        @DisplayName("Should return copy of documents list")
        void shouldReturnCopyOfDocumentsList() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc.pdf");

            documentService.saveDocument("john@techcorp.com", file, DocumentType.CONTRACT);

            List<EmployeeDocument> documents1 = documentService.getDocuments("john@techcorp.com");
            List<EmployeeDocument> documents2 = documentService.getDocuments("john@techcorp.com");

            // Should be different list instances
            assertNotSame(documents1, documents2);
            assertEquals(documents1.size(), documents2.size());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocuments(null)
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocuments("")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null or empty"));
        }
    }

    @Nested
    @DisplayName("Get Document Tests")
    class GetDocumentTest {

        @Test
        @DisplayName("Should return document by ID")
        void shouldReturnDocumentById() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_contract.pdf");

            EmployeeDocument savedDocument = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            Optional<EmployeeDocument> found = documentService.getDocument(
                "john@techcorp.com", savedDocument.getId()
            );

            assertTrue(found.isPresent());
            assertEquals(savedDocument.getId(), found.get().getId());
        }

        @Test
        @DisplayName("Should return empty optional for non-existent document")
        void shouldReturnEmptyOptionalForNonExistentDocument() {
            Optional<EmployeeDocument> found = documentService.getDocument(
                "john@techcorp.com", "non-existent-id"
            );

            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should return empty optional for wrong employee email")
        void shouldReturnEmptyOptionalForWrongEmployeeEmail() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            Optional<EmployeeDocument> found = documentService.getDocument(
                "jane@techcorp.com", document.getId()
            );

            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should normalize email when getting document")
        void shouldNormalizeEmailWhenGettingDocument() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            Optional<EmployeeDocument> found = documentService.getDocument(
                "John@TechCorp.COM", document.getId()
            );

            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocument(null, "doc-id")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocument("", "doc-id")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when document ID is null")
        void shouldThrowExceptionWhenDocumentIdIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocument("john@techcorp.com", null)
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when document ID is empty")
        void shouldThrowExceptionWhenDocumentIdIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.getDocument("john@techcorp.com", "")
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null or empty"));
        }
    }

    @Nested
    @DisplayName("Delete Document Tests")
    class DeleteDocumentTest {

        @Test
        @DisplayName("Should delete document successfully")
        void shouldDeleteDocumentSuccessfully() throws IOException {
            MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_contract.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            documentService.deleteDocument("john@techcorp.com", document.getId());

            Optional<EmployeeDocument> found = documentService.getDocument(
                "john@techcorp.com", document.getId()
            );
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should remove document from list after deletion")
        void shouldRemoveDocumentFromListAfterDeletion() {
            MockMultipartFile file1 = new MockMultipartFile(
                "file", "doc1.pdf", "application/pdf", "content1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "file", "doc2.pdf", "application/pdf", "content2".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid1_doc1.pdf")
                .thenReturn("/uploads/documents/john@techcorp.com/uuid2_doc2.pdf");

            EmployeeDocument doc1 = documentService.saveDocument("john@techcorp.com", file1, DocumentType.CONTRACT);
            EmployeeDocument doc2 = documentService.saveDocument("john@techcorp.com", file2, DocumentType.CERTIFICATE);

            documentService.deleteDocument("john@techcorp.com", doc1.getId());

            List<EmployeeDocument> documents = documentService.getDocuments("john@techcorp.com");
            assertEquals(1, documents.size());
            assertEquals(doc2.getId(), documents.get(0).getId());
        }

        @Test
        @DisplayName("Should normalize email when deleting document")
        void shouldNormalizeEmailWhenDeletingDocument() {
            MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "content".getBytes()
            );

            when(fileStorageService.saveFile(any(), anyString()))
                .thenReturn("/uploads/documents/john@techcorp.com/uuid_doc.pdf");

            EmployeeDocument document = documentService.saveDocument(
                "john@techcorp.com", file, DocumentType.CONTRACT
            );

            assertDoesNotThrow(() -> 
                documentService.deleteDocument("John@TechCorp.COM", document.getId())
            );
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void shouldThrowExceptionWhenDocumentNotFound() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.deleteDocument("john@techcorp.com", "non-existent-id")
            );
            assertTrue(exception.getMessage().contains("Document not found"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.deleteDocument(null, "doc-id")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.deleteDocument("", "doc-id")
            );
            assertTrue(exception.getMessage().contains("Employee email cannot be null or empty"));
        }

        @Test
        @DisplayName("Should throw exception when document ID is null")
        void shouldThrowExceptionWhenDocumentIdIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.deleteDocument("john@techcorp.com", null)
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when document ID is empty")
        void shouldThrowExceptionWhenDocumentIdIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.deleteDocument("john@techcorp.com", "")
            );
            assertTrue(exception.getMessage().contains("Document ID cannot be null or empty"));
        }
    }

    @Test
    @DisplayName("Should handle multiple operations for same employee")
    void shouldHandleMultipleOperationsForSameEmployee() {
        MockMultipartFile file1 = new MockMultipartFile(
            "file", "doc1.pdf", "application/pdf", "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file", "doc2.pdf", "application/pdf", "content2".getBytes()
        );
        MockMultipartFile file3 = new MockMultipartFile(
            "file", "doc3.pdf", "application/pdf", "content3".getBytes()
        );

        when(fileStorageService.saveFile(any(), anyString()))
            .thenReturn("/uploads/documents/john@techcorp.com/uuid1_doc1.pdf")
            .thenReturn("/uploads/documents/john@techcorp.com/uuid2_doc2.pdf")
            .thenReturn("/uploads/documents/john@techcorp.com/uuid3_doc3.pdf");

        // Save 3 documents
        EmployeeDocument doc1 = documentService.saveDocument("john@techcorp.com", file1, DocumentType.CONTRACT);
        EmployeeDocument doc2 = documentService.saveDocument("john@techcorp.com", file2, DocumentType.CERTIFICATE);
        EmployeeDocument doc3 = documentService.saveDocument("john@techcorp.com", file3, DocumentType.ID_CARD);

        // Verify all saved
        assertEquals(3, documentService.getDocuments("john@techcorp.com").size());

        // Delete one
        documentService.deleteDocument("john@techcorp.com", doc2.getId());

        // Verify 2 remaining
        List<EmployeeDocument> remaining = documentService.getDocuments("john@techcorp.com");
        assertEquals(2, remaining.size());

        // Verify correct ones remain
        assertTrue(remaining.stream().anyMatch(d -> d.getId().equals(doc1.getId())));
        assertTrue(remaining.stream().anyMatch(d -> d.getId().equals(doc3.getId())));
        assertFalse(remaining.stream().anyMatch(d -> d.getId().equals(doc2.getId())));
    }
}

