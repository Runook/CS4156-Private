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
}