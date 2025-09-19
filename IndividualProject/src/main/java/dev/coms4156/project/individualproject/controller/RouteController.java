package dev.coms4156.project.individualproject.controller;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller that defines routes for interacting with books.
 *
 * <p>Provides endpoints for retrieving books, checking availability,
 * and adding copies to existing books.
 */
@RestController
public class RouteController {

  private final MockApiService mockApiService;

  public RouteController(MockApiService mockApiService) {
    this.mockApiService = mockApiService;
  }

  @GetMapping({"/", "/index"})
  public String index() {
    return "Welcome to the home page! In order to make an API call direct your browser"
        + "or Postman to an endpoint.";
  }

  /**
   * Returns the details of the specified book.
   *
   * @param id An {@code int} representing the unique identifier of the book to retrieve.
   *
   * @return A {@code ResponseEntity} containing either the matching {@code Book} object with an
   *         HTTP 200 response, or a message indicating that the book was not
   *         found with an HTTP 404 response.
   */
  @GetMapping({"/book/{id}"})
  public ResponseEntity<?> getBook(@PathVariable int id) {
    for (Book book : mockApiService.getBooks()) {
      if (book.getId() == id) {
        return new ResponseEntity<>(book, HttpStatus.OK);
      }
    }

    return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Get and return a list of all the books with available copies.
   *
   * @return A {@code ResponseEntity} containing a list of available {@code Book} objects with an
   *         HTTP 200 response if sucessful, or a message indicating an error occurred with an
   *         HTTP 500 response.
   */
  @PutMapping({"/books/available"})
  public ResponseEntity<?> getAvailableBooks() {
    try {
      ArrayList<Book> availableBooks = new ArrayList<>();

      for (Book book : mockApiService.getBooks()) {
        if (book.hasCopies()) {
          availableBooks.add(book);
        }
      }

      return new ResponseEntity<>(mockApiService.getBooks(), HttpStatus.OK);
    } catch (Exception e) {
      System.err.println(e);
      return new ResponseEntity<>("Error occurred when getting all available books",
          HttpStatus.OK);
    }
  }

  /**
   * Adds a copy to the {@code} Book object if it exists.
   *
   * @param bookId An {@code Integer} representing the unique id of the book.
   * @return A {@code ResponseEntity} containing the updated {@code Book} object with an
   *         HTTP 200 response if successful or HTTP 404 if the book is not found,
   *         or a message indicating an error occurred with an HTTP 500 code.
   */
  @PatchMapping({"/book/{bookId}/add"})
  public ResponseEntity<?> addCopy(@PathVariable Integer bookId) {
    try {
      for (Book book : mockApiService.getBooks()) {
        if (bookId.equals(book.getId())) {
          book.addCopy();
          return new ResponseEntity<>(book, HttpStatus.OK);
        }
      }

      return new ResponseEntity<>("Book not found.", HttpStatus.I_AM_A_TEAPOT);
    } catch (Exception e) {
      System.err.println(e);
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Checks out a book by its ID, updating all relevant fields to indicate checkout.
   *
   * @param bookId An {@code Integer} representing the unique ID of the book to check out.
   * @return A {@code ResponseEntity} containing the updated {@code Book} object with an
   *         HTTP 200 response if successful, or an appropriate error message with
   *         corresponding HTTP status code on failure.
   */
  @PostMapping("/checkout")
  public ResponseEntity<?> checkoutBook(@RequestParam Integer bookId) {
    // Validate bookId parameter
    if (bookId == null || bookId < 0) {
      return new ResponseEntity<>("Invalid book ID", HttpStatus.BAD_REQUEST);
    }

    try {
      // Find the book by ID
      for (Book book : mockApiService.getBooks()) {
        if (bookId.equals(book.getId())) {
          // Attempt to checkout the book
          String dueDate = book.checkoutCopy();
          
          if (dueDate != null) {
            // Checkout successful - update the book in the service
            mockApiService.updateBook(book);
            return new ResponseEntity<>(book, HttpStatus.OK);
          } else {
            // No copies available for checkout
            return new ResponseEntity<>("No copies available for checkout", 
                                      HttpStatus.CONFLICT);
          }
        }
      }

      // Book not found
      return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.err.println(e);
      return new ResponseEntity<>("An error occurred during checkout", 
                                HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
