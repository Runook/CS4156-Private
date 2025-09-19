package dev.coms4156.project.individualproject.controller;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller that defines routes for interacting with books.
 *
 * <p>Provides endpoints for retrieving books, checking availability,
 * and adding copies to existing books.
 */
@RestController
public class RouteController {

  private static final Logger logger = LoggerFactory.getLogger(RouteController.class);
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
    logger.info("Retrieving book with id: {}", id);
    
    if (id < 0) {
      logger.warn("Invalid book id provided: {}", id);
      return new ResponseEntity<>("Invalid book id", HttpStatus.BAD_REQUEST);
    }
    
    try {
      for (Book book : mockApiService.getBooks()) {
        if (book.getId() == id) {
          logger.info("Successfully found book with id: {}", id);
          return new ResponseEntity<>(book, HttpStatus.OK);
        }
      }
      
      logger.info("Book not found with id: {}", id);
      return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      logger.error("Error retrieving book with id: {}", id, e);
      return new ResponseEntity<>("An error occurred while retrieving book", HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
    logger.info("Retrieving all available books");
    
    try {
      ArrayList<Book> availableBooks = new ArrayList<>();

      for (Book book : mockApiService.getBooks()) {
        if (book.hasCopies()) {
          availableBooks.add(book);
        }
      }

      logger.info("Found {} available books out of {} total books", 
                  availableBooks.size(), mockApiService.getBooks().size());
      return new ResponseEntity<>(availableBooks, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error occurred when getting all available books", e);
      return new ResponseEntity<>("Error occurred when getting all available books",
          HttpStatus.INTERNAL_SERVER_ERROR);
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
    logger.info("Adding copy to book with id: {}", bookId);
    
    if (bookId == null || bookId < 0) {
      logger.warn("Invalid book id provided for addCopy: {}", bookId);
      return new ResponseEntity<>("Invalid book id", HttpStatus.BAD_REQUEST);
    }
    
    try {
      for (Book book : mockApiService.getBooks()) {
        if (bookId.equals(book.getId())) {
          int previousTotal = book.getTotalCopies();
          book.addCopy();
          logger.info("Successfully added copy to book with id: {}. Total copies: {} -> {}", 
                     bookId, previousTotal, book.getTotalCopies());
          return new ResponseEntity<>(book, HttpStatus.OK);
        }
      }

      logger.info("Book not found when trying to add copy. Book id: {}", bookId);
      return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      logger.error("Error adding copy to book with id: {}", bookId, e);
      return new ResponseEntity<>("An error occurred while adding copy", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns a list of exactly 10 unique book recommendations.
   * Half are the most popular books (based on checkout count) and half are randomly selected.
   *
   * @return A {@code ResponseEntity} containing a list of 10 recommended {@code Book} objects
   *         with an HTTP 200 response if successful, or an appropriate error message with
   *         corresponding HTTP status code on failure.
   */
  @GetMapping("/books/recommendation")
  public ResponseEntity<?> getRecommendation() {
    logger.info("Retrieving book recommendations");
    
    try {
      ArrayList<Book> allBooks = mockApiService.getBooks();
      
      // Check if we have enough books
      if (allBooks.size() < 10) {
        logger.warn("Insufficient books for recommendation. Available: {}, Required: 10", 
                   allBooks.size());
        return new ResponseEntity<>("Insufficient books available for recommendation", 
                                  HttpStatus.SERVICE_UNAVAILABLE);
      }
      
      // Create a copy to avoid modifying the original list
      ArrayList<Book> booksCopy = new ArrayList<>(allBooks);
      
      // Sort by popularity (most checked out first)
      booksCopy.sort((a, b) -> Integer.compare(b.getAmountOfTimesCheckedOut(), 
                                              a.getAmountOfTimesCheckedOut()));
      
      // Get top 5 most popular books
      ArrayList<Book> recommendedBooks = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        recommendedBooks.add(booksCopy.get(i));
      }
      
      // Remove the top 5 from the copy so we don't select them again
      for (int i = 0; i < 5; i++) {
        booksCopy.remove(0);
      }
      
      // Randomly select 5 books from the remaining books
      Random random = new Random();
      for (int i = 0; i < 5; i++) {
        int randomIndex = random.nextInt(booksCopy.size());
        recommendedBooks.add(booksCopy.get(randomIndex));
        booksCopy.remove(randomIndex); // Remove to ensure uniqueness
      }
      
      logger.info("Successfully generated recommendation with {} books", recommendedBooks.size());
      return new ResponseEntity<>(recommendedBooks, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error occurred while generating book recommendations", e);
      return new ResponseEntity<>("An error occurred while generating recommendations", 
                                HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
