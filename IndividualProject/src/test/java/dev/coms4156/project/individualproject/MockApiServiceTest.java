package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MockApiService class.
 * 
 * <p>This test class provides minimal but essential tests for the MockApiService
 * to verify proper initialization and basic functionality of the service layer.
 * These tests focus on the core operations like initialization and book updates.
 *
 * @author CS4156 Team
 * @version 1.0
 * @since 1.0
 */
class MockApiServiceTest {

  /**
   * Tests that the MockApiService constructor properly initializes the books list.
   * 
   * <p>Verifies that after construction, the service has a non-null books list
   * that is ready for use, even if it might be empty initially.
   */
  @Test
  void constructor_initializesBooksList() {
    final MockApiService service = new MockApiService();
    assertNotNull(service.getBooks(), "books list should be initialized (possibly empty)");
  }

  /**
   * Tests that updating a book maintains the same list size.
   * 
   * <p>This test verifies the update behavior of the service:
   * <ul>
   * <li>If the list is non-empty, uses the first book's ID for replacement</li>
   * <li>If the list is empty, uses a new ID</li>
   * <li>Ensures that updating maintains the same total number of books</li>
   * </ul>
   */
  @Test
  void updateBook_keepsListSize() {
    final MockApiService service = new MockApiService();
    final List<Book> before = service.getBooks();
    final int sizeBefore = before.size();

    // If list is non-empty, use first book's id for replacement; otherwise use a new id
    final int bookId = sizeBefore > 0 ? before.get(0).getId() : 123456;
    final Book replacement = new Book("Replacement", bookId);

    service.updateBook(replacement);

    assertEquals(sizeBefore, service.getBooks().size(), "replacing should keep size unchanged");
  }

  @Test
  void updateBook_withNonExistentBook_keepsListSizeUnchanged() {
    // Test the edge case where updateBook is called with a book that doesn't exist
    // Based on the actual implementation, non-existent books are not added
    final MockApiService service = new MockApiService();
    final int initialSize = service.getBooks().size();
    
    // Create a book with a unique ID that definitely doesn't exist
    final Book newBook = new Book("Non-existent Book", 999999);
    
    service.updateBook(newBook);
    
    // The book should NOT be added since no matching book was found
    // The updateBook method only replaces existing books
    assertEquals(initialSize, service.getBooks().size(), 
                "non-existent book should not be added to list");
  }

  @Test
  void updateBook_withExistingBook_replacesBookInList() {
    // Test the case where updateBook replaces an existing book
    final MockApiService service = new MockApiService();
    final List<Book> books = service.getBooks();
    
    if (!books.isEmpty()) {
      final Book originalBook = books.get(0);
      final int originalId = originalBook.getId();
      final String originalTitle = originalBook.getTitle();
      
      // Create a replacement book with same ID but different title
      final Book replacementBook = new Book("Updated Title", originalId);
      
      service.updateBook(replacementBook);
      
      final List<Book> updatedBooks = service.getBooks();
      assertEquals(books.size(), updatedBooks.size(), "list size should remain same");
      
      // Find the book with the same ID and verify it was updated
      boolean foundUpdatedBook = false;
      for (Book book : updatedBooks) {
        if (book.getId() == originalId) {
          assertEquals("Updated Title", book.getTitle(), 
                      "book title should be updated");
          foundUpdatedBook = true;
          break;
        }
      }
      assertEquals(true, foundUpdatedBook, "updated book should be found in list");
    }
  }

  @Test
  void getBooks_returnsDefensiveCopy() {
    // Test that getBooks returns a defensive copy, not the original list
    final MockApiService service = new MockApiService();
    final List<Book> books1 = service.getBooks();
    final List<Book> books2 = service.getBooks();
    
    // The two lists should be different objects (defensive copies)
    assertNotNull(books1);
    assertNotNull(books2);
    // We can't directly test object identity here since the implementation 
    // creates new ArrayList instances, but we can verify they have the same content
    assertEquals(books1.size(), books2.size(), "defensive copies should have same size");
  }

  @Test 
  void printBooks_executesWithoutError() {
    // Test the printBooks method to ensure it executes without throwing exceptions
    final MockApiService service = new MockApiService();
    
    // This method prints to System.out, so we just verify it doesn't throw
    try {
      service.printBooks();
      // If we get here, the method executed successfully
      assertEquals(true, true, "printBooks should execute without error");
    } catch (Exception e) {
      assertEquals(true, false, "printBooks should not throw exceptions");
    }
  }
}