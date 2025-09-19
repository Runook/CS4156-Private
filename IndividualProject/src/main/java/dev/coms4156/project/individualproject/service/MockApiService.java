package dev.coms4156.project.individualproject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.coms4156.project.individualproject.model.Book;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 *  This class defines the Mock API Service mimicking CLIO's database. It defines
 * useful methods for accessing or modifying books.
 */
@Service
public class MockApiService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockApiService.class);
  private List<Book> books;
  private List<String> bags;

  /**
   * Constructs a new {@code MockApiService} and loads book data from a JSON file located at
   * {@code resources/mockdata/books.json}.
   * If the file is not found, an empty list of books is initialized. If the file is found but
   * cannot be parsed, an error message is printed and no data is loaded.
   */
  public MockApiService() {
    try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("mockdata/books.json")) {
      if (inputStream == null) {
        LOGGER.error("Failed to find mockdata/books.json in resources.");
        books = new ArrayList<>(0);
      } else {
        final ObjectMapper mapper = new ObjectMapper();
        books = mapper.readValue(inputStream, new TypeReference<ArrayList<Book>>(){});
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Successfully loaded {} books from mockdata/books.json", books.size());
        }
      }
    } catch (final Exception e) {
      LOGGER.error("Failed to load books from JSON file", e);
      books = new ArrayList<>(0);
    }
  }

  public List<Book> getBooks() {
    return new ArrayList<>(books);
  }

  /**
   * Updates the stored list of books by replacing the existing book that matches the given.
   * {@code newBook} with the updated version
   *
   * @param newBook A {@code Book} object containing the updated information
   *                to replace the existing entry.
   */

  public void updateBook(final Book newBook) {
    final List<Book> tmpBooks = new ArrayList<>();
    for (final Book book : books) {
      if (book.equals(newBook)) {
        tmpBooks.add(newBook);
      } else {
        tmpBooks.add(book);
      }
    }

    this.books = tmpBooks;
  }

  public void printBooks() {
    books.forEach(System.out::println);
  }
}
