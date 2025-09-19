package dev.coms4156.project.individualproject;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.coms4156.project.individualproject.controller.RouteController;
import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer integration tests for the RouteController class.
 * 
 * <p>This test class uses Spring Boot's {@link WebMvcTest} to test the web layer
 * in isolation. It focuses on testing the HTTP endpoints, request/response handling,
 * status codes, and JSON responses without loading the full application context.
 * The MockApiService is mocked to control the service layer behavior during testing.
 *
 * @author CS4156 Team
 * @version 1.0
 * @since 1.0
 */
@WebMvcTest(RouteController.class)
class RouteControllerTest {

  /** MockMvc instance for performing HTTP requests in tests. */
  @Autowired private MockMvc mvc;
  
  /** Mocked MockApiService to control service layer behavior during testing. */
  @MockBean private MockApiService mockApiService;

  /**
   * Tests that both index endpoints return a welcome message.
   * 
   * <p>Verifies that both the root endpoint ("/") and the explicit index endpoint
   * ("/index") return HTTP 200 OK status and contain a "Welcome" message in
   * the response body.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void indexEndpointsReturnWelcome() throws Exception {
    mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Welcome")));
    mvc.perform(get("/index"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Welcome")));
  }

  /**
   * Tests the getBook endpoint for both successful and failed book retrieval.
   * 
   * <p>This test verifies two scenarios:
   * <ul>
   * <li>When a book exists: returns HTTP 200 OK with correct JSON response
   * containing the book's ID and title</li>
   * <li>When a book doesn't exist: returns HTTP 404 Not Found with an
   * appropriate error message</li>
   * </ul>
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getBook_found_and_notFound() throws Exception {
    Book b = new Book("Hello", 1);
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(b)));

    mvc.perform(get("/book/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Hello"));

    // Re-stub (optional, previous return value still applies)
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(b)));

    mvc.perform(get("/book/2"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Book not found")));
  }

  /**
   * Tests the getAvailableBooks endpoint returns only available books.
   * 
   * <p>This test verifies that the endpoint correctly filters and returns
   * only books that have available copies (copiesAvailable > 0).
   * Books with no available copies should not be included in the response.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getAvailableBooks_filtersAvailableBooks() throws Exception {
    Book b1 = new Book("A", 1);
    Book b2 = new Book("B", 2);
    b2.deleteCopy(); // Make b2 have no available copies
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(b1, b2)));

    mvc.perform(put("/books/available"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("A"))
            .andExpect(jsonPath("$[0].copiesAvailable").value(1))
            .andExpect(jsonPath("$[1]").doesNotExist()); // b2 should not be returned
  }

  /**
   * Tests the addCopy endpoint for both successful and unsuccessful scenarios.
   * 
   * <p>This test covers two cases:
   * <ul>
   * <li>Success case: When the book exists, adding a copy increases both
   * available copies and total copies counts, returning HTTP 200 OK with
   * updated book data</li>
   * <li>Not found case: When the book doesn't exist, the current implementation
   * returns HTTP 418 (I'm a teapot) status code</li>
   * </ul>
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void addCopy_success_and_notFound418() throws Exception {
    Book book = new Book("A", 10);
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));

    mvc.perform(patch("/book/10/add").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.copiesAvailable").value(2))
            .andExpect(jsonPath("$.totalCopies").value(2));

    // Not found: current implementation returns 418
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));
    mvc.perform(patch("/book/999/add").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(418));
  }

  // ========== Tests for /books/recommendation endpoint ==========

  /**
   * Tests the book recommendation endpoint with sufficient books.
   * 
   * <p>Verifies that the endpoint returns exactly 10 books when there are
   * enough books available in the system.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getRecommendations_sufficientBooks_returns10Books() throws Exception {
    final List<Book> books = createBooksForRecommendation(15);
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(books));

    mvc.perform(get("/books/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(10));
  }

  /**
   * Tests the book recommendation endpoint with insufficient books.
   * 
   * <p>Verifies that when there are fewer than 10 books available,
   * the endpoint returns all available books.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getRecommendations_insufficientBooks_returnsAllAvailable() throws Exception {
    final List<Book> books = createBooksForRecommendation(5);
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(books));

    mvc.perform(get("/books/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5));
  }

  /**
   * Tests the book recommendation endpoint with no books.
   * 
   * <p>Verifies that when no books are available, the endpoint
   * returns an empty list.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getRecommendations_noBooks_returnsEmptyList() throws Exception {
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>());

    mvc.perform(get("/books/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
  }

  /**
   * Tests the book recommendation endpoint ensures popularity ordering.
   * 
   * <p>Verifies that popular books (higher checkout counts) are included
   * in recommendations when there are exactly 10 books.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getRecommendations_includesPopularBooks() throws Exception {
    final List<Book> books = new ArrayList<>();
    
    // Create 5 highly popular books (checked out multiple times)
    for (int i = 1; i <= 5; i++) {
      final Book book = new Book("Popular Book " + i, i);
      for (int j = 0; j < (10 - i); j++) { // Different popularity levels
        book.checkoutCopy();
        book.returnCopy(book.getReturnDates().get(0));
      }
      books.add(book);
    }
    
    // Create 5 less popular books (never checked out)
    for (int i = 6; i <= 10; i++) {
      books.add(new Book("Regular Book " + i, i));
    }
    
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(books));

    mvc.perform(get("/books/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(10))
            .andExpect(jsonPath("$[0].title").value(containsString("Popular")));
  }

  // ========== Tests for /checkout endpoint ==========

  /**
   * Tests successful book checkout with valid book ID.
   * 
   * <p>Verifies that checking out a book with available copies
   * returns HTTP 200 OK with the updated book object.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_validId_returnsUpdatedBook() throws Exception {
    final Book book = new Book("Test Book", 1);
    book.addCopy(); // Ensure there's a copy to checkout
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));

    mvc.perform(post("/checkout").param("bookId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Book"))
            .andExpect(jsonPath("$.copiesAvailable").value(1)) // Should decrease
            .andExpect(jsonPath("$.amountOfTimesCheckedOut").value(1)); // Should increase
  }

  /**
   * Tests book checkout with invalid book ID.
   * 
   * <p>Verifies that attempting to checkout a non-existent book
   * returns HTTP 404 Not Found with appropriate error message.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_invalidId_returns404() throws Exception {
    final Book book = new Book("Test Book", 1);
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));

    mvc.perform(post("/checkout").param("bookId", "999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Book not found")));
  }

  /**
   * Tests book checkout with null book ID.
   * 
   * <p>Verifies that attempting to checkout with null book ID
   * returns HTTP 400 Bad Request with appropriate error message.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_nullId_returns400() throws Exception {
    mvc.perform(post("/checkout"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid book ID")));
  }

  /**
   * Tests book checkout with negative book ID.
   * 
   * <p>Verifies that attempting to checkout with negative book ID
   * returns HTTP 400 Bad Request with appropriate error message.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_negativeId_returns400() throws Exception {
    mvc.perform(post("/checkout").param("bookId", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid book ID")));
  }

  /**
   * Tests book checkout when no copies are available.
   * 
   * <p>Verifies that attempting to checkout a book with no available copies
   * returns HTTP 409 Conflict with appropriate error message.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_noCopiesAvailable_returns409() throws Exception {
    final Book book = new Book("Test Book", 1);
    book.deleteCopy(); // Remove the only copy to make it unavailable
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));

    mvc.perform(post("/checkout").param("bookId", "1"))
            .andExpect(status().isConflict())
            .andExpect(content().string(containsString("No copies available")));
  }

  /**
   * Tests checkout endpoint with edge case of zero ID.
   * 
   * <p>Verifies that zero is treated as an invalid book ID and
   * returns HTTP 400 Bad Request.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_zeroId_returns400() throws Exception {
    mvc.perform(post("/checkout").param("bookId", "0"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid book ID")));
  }

  /**
   * Tests multiple checkout operations on the same book.
   * 
   * <p>Verifies that multiple checkout operations correctly
   * decrement available copies and increment checkout count.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void checkoutBook_multipleCheckouts_updatesCountsCorrectly() throws Exception {
    final Book book = new Book("Multi Checkout Book", 1);
    book.addCopy();
    book.addCopy(); // Now has 3 total copies
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(book)));

    // First checkout
    mvc.perform(post("/checkout").param("bookId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.copiesAvailable").value(2))
            .andExpect(jsonPath("$.amountOfTimesCheckedOut").value(1));

    // Second checkout - the book state should already be updated by the first checkout
    mvc.perform(post("/checkout").param("bookId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.copiesAvailable").value(1))
            .andExpect(jsonPath("$.amountOfTimesCheckedOut").value(2));
  }

  // ========== Helper methods ==========

  /**
   * Creates a list of books for recommendation testing.
   * 
   * @param count Number of books to create
   * @return List of books with varied checkout histories
   */
  private List<Book> createBooksForRecommendation(final int count) {
    final List<Book> books = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      final Book book = new Book("Book " + i, i);
      // Give some books different popularity levels
      for (int j = 0; j < (i % 5); j++) {
        book.checkoutCopy();
        if (!book.getReturnDates().isEmpty()) {
          book.returnCopy(book.getReturnDates().get(0));
        }
      }
      books.add(book);
    }
    return books;
  }
}