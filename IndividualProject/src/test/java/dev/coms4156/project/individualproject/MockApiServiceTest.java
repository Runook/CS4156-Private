package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
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
    MockApiService s = new MockApiService();
    assertNotNull(s.getBooks(), "books list should be initialized (possibly empty)");
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
    MockApiService s = new MockApiService();
    ArrayList<Book> before = s.getBooks();
    int sizeBefore = before.size();

    // If list is non-empty, use first book's id for replacement; otherwise use a new id
    int id = sizeBefore > 0 ? before.get(0).getId() : 123456;
    Book replacement = new Book("Replacement", id);

    s.updateBook(replacement);

    assertEquals(sizeBefore, s.getBooks().size(), "replacing should keep size unchanged");
  }
}