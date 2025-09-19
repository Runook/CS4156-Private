package dev.coms4156.project.individualproject.controller;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);
  private static final int MIN_BOOKS_FOR_REC = 10;
  private static final int MIN_VALID_BOOK_ID = 1;
  private final MockApiService mockApiService;

  public RouteController(final MockApiService mockApiService) {
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
   * @param bookId An {@code int} representing the unique identifier of the book to retrieve.
   *
   * @return A {@code ResponseEntity} containing either the matching {@code Book} object with an
   *         HTTP 200 response, or a message indicating that the book was not
   *         found with an HTTP 404 response.
   */
  @GetMapping({"/book/{bookId}"})
  public ResponseEntity<?> getBook(@PathVariable final int bookId) {
    ResponseEntity<?> result = new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
    
    for (final Book book : mockApiService.getBooks()) {
      if (book.getId() == bookId) {
        result = new ResponseEntity<>(book, HttpStatus.OK);
        break;
      }
    }

    return result;
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
    ResponseEntity<?> result;
    
    try {
      final ArrayList<Book> availableBooks = new ArrayList<>();

      for (final Book book : mockApiService.getBooks()) {
        if (book.hasCopies()) {
          availableBooks.add(book);
        }
      }

      result = new ResponseEntity<>(availableBooks, HttpStatus.OK);
    } catch (final Exception e) {
      LOGGER.error("Error occurred when getting all available books", e);
      result = new ResponseEntity<>("Error occurred when getting all available books",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    return result;
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
  public ResponseEntity<?> addCopy(@PathVariable final Integer bookId) {
    ResponseEntity<?> result = ResponseEntity.notFound().build();
    
    try {
      for (final Book book : mockApiService.getBooks()) {
        if (bookId.equals(book.getId())) {
          book.addCopy();
          result = new ResponseEntity<>(book, HttpStatus.OK);
          break;
        }
      }

      if (result.equals(ResponseEntity.notFound().build())) {
        result = new ResponseEntity<>("Book not found.", HttpStatus.I_AM_A_TEAPOT);
      }
    } catch (final Exception e) {
      LOGGER.error("Error occurred during addCopy operation", e);
      result = ResponseEntity.notFound().build();
    }
    
    return result;
  }

  /**
   * Checkout a book by updating its checkout information.
   *
   * @param bookId The ID of the book to checkout
   * @return ResponseEntity with the updated book or error message
   */
  @PostMapping("/checkout")
  public ResponseEntity<?> checkoutBook(@RequestParam(required = false) final Integer bookId) {
    ResponseEntity<?> result;

    if (bookId == null || bookId < MIN_VALID_BOOK_ID) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Invalid book ID provided for checkout: {}", bookId);
      }
      result = new ResponseEntity<>("Invalid book ID", HttpStatus.BAD_REQUEST);
    } else {
      result = performCheckout(bookId);
    }

    return result;
  }

  /**
   * Helper method to perform the actual checkout operation.
   *
   * @param bookId The ID of the book to checkout
   * @return ResponseEntity with the result
   */
  private ResponseEntity<?> performCheckout(final Integer bookId) {
    ResponseEntity<?> result;

    try {
      result = processBookCheckout(bookId);
    } catch (final Exception e) {
      LOGGER.error("Error occurred during checkout for book ID: {}", bookId, e);
      result = new ResponseEntity<>("An error occurred during checkout", 
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  /**
   * Process the book checkout operation.
   *
   * @param bookId The ID of the book to checkout
   * @return ResponseEntity with the result
   */
  private ResponseEntity<?> processBookCheckout(final Integer bookId) {
    final ResponseEntity<?> notFound = 
        new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    final ResponseEntity<?> noAvailable = 
        new ResponseEntity<>("No copies available for checkout", HttpStatus.CONFLICT);
    
    ResponseEntity<?> result = notFound;
    Book foundBook = null;

    // Find the book without creating objects in loop
    for (final Book book : mockApiService.getBooks()) {
      if (bookId.equals(book.getId())) {
        foundBook = book;
        break;
      }
    }

    // Process the found book outside of the loop
    if (foundBook != null) {
      if (foundBook.getCopiesAvailable() > 0) {
        foundBook.checkoutCopy();
        mockApiService.updateBook(foundBook);
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Successfully checked out book with ID: {}", bookId);
        }
        result = new ResponseEntity<>(foundBook, HttpStatus.OK);
      } else {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("No copies available for checkout for book ID: {}", bookId);
        }
        result = noAvailable;
      }
    }

    return result;
  }

  /**
   * Get book recommendations - 5 most popular + 5 random books.
   *
   * @return ResponseEntity containing a list of 10 recommended books
   */
  @GetMapping("/books/recommendation")
  public ResponseEntity<?> getBookRecommendations() {
    ResponseEntity<?> result;

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Retrieving book recommendations");
    }

    try {
      result = generateRecommendations();
    } catch (final Exception e) {
      LOGGER.error("Error generating book recommendations", e);
      result = new ResponseEntity<>("Error generating recommendations", 
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  /**
   * Generate book recommendations.
   *
   * @return ResponseEntity with recommendations
   */
  private ResponseEntity<?> generateRecommendations() {
    final List<Book> allBooks = mockApiService.getBooks();
    ResponseEntity<?> result;

    if (allBooks.isEmpty()) {
      result = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    } else if (allBooks.size() < MIN_BOOKS_FOR_REC) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Not enough books available for recommendations. Found: {}", allBooks.size());
      }
      // Return all available books instead of an error
      final List<Book> recommendations = createLimitedRecommendationList(allBooks);
      result = new ResponseEntity<>(recommendations, HttpStatus.OK);
    } else {
      final List<Book> recommendations = createRecommendationList(allBooks);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Successfully generated recommendation with {} books", recommendations.size());
      }
      result = new ResponseEntity<>(recommendations, HttpStatus.OK);
    }

    return result;
  }

  /**
   * Create the recommendation list with 5 popular + 5 random books.
   *
   * @param allBooks List of all available books
   * @return List of recommended books
   */
  private List<Book> createRecommendationList(final List<Book> allBooks) {
    final List<Book> sortedBooks = new ArrayList<>(allBooks);
    sortedBooks.sort((book1, book2) -> 
        Integer.compare(book2.getAmountOfTimesCheckedOut(), book1.getAmountOfTimesCheckedOut()));

    final List<Book> recommendations = new ArrayList<>();
    final List<Book> topFive = sortedBooks.subList(0, 5);
    recommendations.addAll(topFive);

    final List<Book> remainingBooks = new ArrayList<>(sortedBooks.subList(5, sortedBooks.size()));
    Collections.shuffle(remainingBooks, new Random());
    recommendations.addAll(remainingBooks.subList(0, 5));

    return recommendations;
  }

  /**
   * Create a limited recommendation list when fewer than 10 books are available.
   *
   * @param allBooks List of all available books (less than 10)
   * @return List of all available books sorted by popularity
   */
  private List<Book> createLimitedRecommendationList(final List<Book> allBooks) {
    final List<Book> sortedBooks = new ArrayList<>(allBooks);
    sortedBooks.sort((book1, book2) -> 
        Integer.compare(book2.getAmountOfTimesCheckedOut(), book1.getAmountOfTimesCheckedOut()));
    return sortedBooks;
  }

}
