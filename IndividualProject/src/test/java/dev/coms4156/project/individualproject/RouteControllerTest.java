package dev.coms4156.project.individualproject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
   * Tests the getAvailableBooks endpoint's current behavior of returning all books.
   * 
   * <p>This test documents the current implementation behavior where the endpoint
   * returns all books from the service, regardless of their availability status.
   * The test includes one book with available copies and one without available
   * copies to demonstrate that both are returned in the response.
   *
   * @throws Exception if the HTTP request fails
   */
  @Test
  void getAvailableBooks_currentBehavior_returnsServiceList() throws Exception {
    Book b1 = new Book("A", 1);
    Book b2 = new Book("B", 2);
    b2.deleteCopy(); // Make b2 have no available copies
    when(mockApiService.getBooks()).thenReturn(new ArrayList<>(List.of(b1, b2)));

    mvc.perform(put("/books/available"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
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
}