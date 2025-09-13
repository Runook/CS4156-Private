package dev.coms4156.project.individualproject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.coms4156.project.individualproject.model.Book;
import java.io.InputStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 *  This class defines the Mock API Service mimicking CLIO's database. It defines
 * useful methods for accessing or modifying books.
 */
@Service
public class MockApiService {

  private static final Logger logger = LoggerFactory.getLogger(MockApiService.class);
  private ArrayList<Book> books;
  private ArrayList<String> bags;

  /**
   * Constructs a new {@code MockApiService} and loads book data from a JSON file located at
   * {@code resources/mockdata/books.json}.
   * If the file is not found, an empty list of books is initialized. If the file is found but
   * cannot be parsed, an error message is printed and no data is loaded.
   */
  public MockApiService() {
    try (InputStream is = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("mockdata/books.json")) {
      if (is == null) {
        logger.warn("Could not find mockdata/books.json in classpath");
        books = new ArrayList<>(0);
      } else {
        ObjectMapper mapper = new ObjectMapper();
        books = mapper.readValue(is, new TypeReference<ArrayList<Book>>(){});
        logger.info("Successfully loaded {} books from mockdata/books.json", books.size());
      }
    } catch (Exception e) {
      logger.error("Failed to load books from mockdata/books.json", e);
      books = new ArrayList<>(0);
    }
  }

  public ArrayList<Book> getBooks() {
    return books;
  }

  /**
   * Updates the stored list of books by replacing the existing book that matches the given.
   * {@code newBook} with the updated version
   *
   * @param newBook A {@code Book} object containing the updated information
   *                to replace the existing entry.
   */

  public void updateBook(Book newBook) {
    ArrayList<Book> tmpBooks = new ArrayList<>();
    for (Book book : books) {
      if (book.equals(newBook)) {
        tmpBooks.add(newBook);
      } else {
        tmpBooks.add(book);
      }
    }

    this.books = this.books;
  }

  public void printBooks() {
    books.forEach(System.out::println);
  }
}
