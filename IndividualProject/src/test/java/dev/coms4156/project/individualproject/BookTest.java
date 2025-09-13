package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.individualproject.model.Book;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Book model class.
 * 
 * <p>This test class provides comprehensive testing for the Book class,
 * including tests for copy management, checkout/return operations,
 * author handling, and object equality/comparison methods.
 *
 * @author CS4156 Team
 * @version 1.0
 * @since 1.0
 */
class BookTest {

  /** The Book instance used for testing. */
  private Book book;

  /**
   * Sets up the test fixture before each test method.
   * Creates a new Book instance with title "When Breath Becomes Air" and ID 0.
   */
  @BeforeEach
  void setUp() {
    book = new Book("When Breath Becomes Air", 0);
  }

  /**
   * Tests the hasCopies() method and deleteCopy() method behavior.
   * 
   * <p>Verifies that:
   * <ul>
   * <li>A new book has copies available</li>
   * <li>Deleting a copy when copies are available returns true</li>
   * <li>After deleting the only copy, no copies remain</li>
   * <li>Attempting to delete when no copies exist returns false</li>
   * </ul>
   */
  @Test
  void hasCopies_thenDelete_toFalse() {
    assertTrue(book.hasCopies());      // copies available
    assertTrue(book.deleteCopy());     // delete 1 copy
    assertFalse(book.hasCopies());     // no copies
    assertFalse(book.deleteCopy());    // delete false
  }

  /**
   * Tests the addCopy() method to ensure it correctly increases both counters.
   * 
   * <p>Verifies that adding a copy increases both the available copies
   * count and the total copies count by 1.
   */
  @Test
  void addCopy_increasesBothCounters() {
    book.addCopy();
    assertEquals(2, book.getCopiesAvailable());
    assertEquals(2, book.getTotalCopies());
  }

  /**
   * Tests the checkoutCopy() method for both successful and failed checkout scenarios.
   * 
   * <p>Verifies that:
   * <ul>
   * <li>Checking out when copies are available returns a non-null due date</li>
   * <li>Available copies count decreases after checkout</li>
   * <li>Checkout counter increases after successful checkout</li>
   * <li>Attempting to checkout when no copies are available returns null</li>
   * </ul>
   */
  @Test
  void checkoutCopy_success_then_null_when_empty() {
    String due = book.checkoutCopy();  // has copies → successful
    assertNotNull(due);
    assertEquals(0, book.getCopiesAvailable());
    assertEquals(1, book.getAmountOfTimesCheckedOut());

    assertNull(book.checkoutCopy());   // no copies → null
  }

  /**
   * Tests the returnCopy() method covering all possible execution branches.
   * 
   * <p>Verifies return copy behavior for:
   * <ul>
   * <li>Null due date parameter</li>
   * <li>Valid due date but no checked out copies</li>
   * <li>Valid return of a previously checked out copy</li>
   * <li>Attempting to return with non-existent due date</li>
   * </ul>
   */
  @Test
  void returnCopy_allBranches() {
    assertFalse(book.returnCopy(null));      // null
    assertFalse(book.returnCopy("2025-01-01")); // list null

    String due = book.checkoutCopy();
    assertTrue(book.returnCopy(due));        // return turn
    assertFalse(book.returnCopy("not-exist")); // not exist for date
  }

  /**
   * Tests the hasMultipleAuthors() method for both single and multiple author scenarios.
   * 
   * <p>Verifies that:
   * <ul>
   * <li>A book with default authors returns false for multiple authors</li>
   * <li>A book with multiple authors returns true</li>
   * </ul>
   */
  @Test
  void hasMultipleAuthors_true_and_false() {
    assertFalse(book.hasMultipleAuthors());
    ArrayList<String> authors = new ArrayList<>();
    authors.add("A");
    authors.add("B");
    book.setAuthors(authors);
    assertTrue(book.hasMultipleAuthors());
  }

  /**
   * Tests that setter methods handle null values correctly by applying default values.
   * 
   * <p>Verifies that when null values are passed to setters, appropriate
   * default values are used instead of storing null references.
   */
  @Test
  void setters_nullBranches_defaultsApplied() {
    book.setLanguage(null);
    book.setShelvingLocation(null);
    book.setSubjects(null);
    book.setAuthors(null);
    book.setReturnDates(null);

    assertEquals("", book.getLanguage());
    assertEquals("", book.getShelvingLocation());
    assertNotNull(book.getAuthors());
    assertNotNull(book.getSubjects());
    assertNotNull(book.getReturnDates());
  }

  /**
   * Tests the equals(), hashCode(), and compareTo() methods for proper object comparison.
   * 
   * <p>Verifies the contract for object equality and comparison:
   * <ul>
   * <li>Reflexivity: an object equals itself</li>
   * <li>Null handling: object does not equal null</li>
   * <li>Type checking: object does not equal objects of different types</li>
   * <li>ID-based equality: books with same ID are equal</li>
   * <li>ID-based inequality: books with different IDs are not equal</li>
   * <li>Hash code consistency: equal objects have equal hash codes</li>
   * <li>Comparison ordering: objects are ordered by ID</li>
   * </ul>
   */
  @Test
  void equals_hash_compareTo() {
    final Book sameId = new Book("Other", 0);
    final Book diffId = new Book("X", 2);

    assertEquals(book, book);                 // same reference
    assertNotEquals(book, null);              // null
    assertFalse(book.equals("not a book"));   // different type
    assertEquals(book, sameId);               // same id
    assertNotEquals(book, diffId);            // different id
    assertEquals(book.hashCode(), sameId.hashCode());
    assertTrue(book.compareTo(diffId) < 0);
  }
}