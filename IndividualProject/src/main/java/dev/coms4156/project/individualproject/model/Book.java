package dev.coms4156.project.individualproject.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the Book entry model.
 */
public class Book implements Comparable<Book> {
  private String title;
  private List<String> authors;
  private String language;
  private String shelvingLocation;
  private String publicationDate;
  private String publisher;
  private List<String> subjects;
  private int bookId;
  private int amountOfTimesCheckedOut;
  private int copiesAvailable;
  private List<String> returnDates;
  private int totalCopies;

  /**
   * Very basic Book constructor.
   *
   * @param title The title of the book.
   * @param bookId The id of the book.
   */
  public Book(final String title, final int bookId) {
    this.title = title;
    this.bookId = bookId;
    this.authors = new ArrayList<>();
    this.language = "";
    this.shelvingLocation = "";
    this.publicationDate = "";
    this.publisher = "";
    this.subjects = new ArrayList<>();
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = 1;
    this.returnDates = new ArrayList<>();
    this.totalCopies = 1;
  }

  /**
   * Complete Book constructor.
   *
   * @param title title of the book.
   * @param authors list of author(s).
   * @param language language of the book.
   * @param shelvingLocation shelving location of the book.
   * @param publicationDate publication date of the book.
   * @param publisher publisher of the book.
   * @param subjects list of subject(s) of the book.
   * @param bookId unique id of the book.
   * @param copiesAvailable number of copies available of the book.
   * @param totalCopies number of available and checked-out copies of the book.
   */
  public Book(final String title, final List<String> authors, final String language, 
              final String shelvingLocation, final String publicationDate, 
              final String publisher, final List<String> subjects,
              final int bookId, final int copiesAvailable, final int totalCopies) {
    this.title = title;
    this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
    this.language = language;
    this.shelvingLocation = shelvingLocation;
    this.publicationDate = publicationDate;
    this.publisher = publisher;
    this.subjects = subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();
    this.bookId = bookId;
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = copiesAvailable;
    this.returnDates = new ArrayList<>();
    this.totalCopies = totalCopies;
  }

  /**
   * No args constructor for Jackson.
   */
  public Book() {
    this.authors = new ArrayList<>();
    this.subjects = new ArrayList<>();
    this.returnDates = new ArrayList<>();
    this.language = "";
    this.shelvingLocation = "";
    this.publicationDate = "";
    this.publisher = "";
    this.title = "";
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = 1;
    this.totalCopies = 1;
    this.bookId = 0;
  }

  public boolean hasCopies() {
    return copiesAvailable > 0;
  }

  public boolean hasMultipleAuthors() {
    return authors.size() > 1;
  }

  /**
   * Deletes a single copy of the book if at least one copy exists and is available.
   *
   * @return {@code true} if a copy was successfully deleted; {@code false} if no copies
   *         are available or exist to delete.
   */

  public boolean deleteCopy() {
    final boolean result;
    if (totalCopies > 0 && copiesAvailable > 0) {
      totalCopies--;
      copiesAvailable--;
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  public void addCopy() {
    totalCopies++;
    copiesAvailable++;
  }

  /**
   * Checks out a copy of the book if available and generates a due date two weeks from today.
   *
   * @return A {@code String} representing the due date if the checkout is successful;
   *         otherwise, {@code null} if no copies are available.
   */

  public String checkoutCopy() {
    String result = null;
    if (copiesAvailable > 0) {
      copiesAvailable--;
      amountOfTimesCheckedOut++;
      final LocalDate today = LocalDate.now();
      final LocalDate dueDate = today.plusWeeks(2);
      final String dueDateStr = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
      returnDates.add(dueDateStr);
      result = dueDateStr;
    }
    return result;
  }

  /**
   * Returns a previously checked-out copy of the book corresponding to the given due date.
   *
   * @param date A {@code String} representing the due date of the book being returned.
   * @return {@code true} if the return was successful and a matching date was removed;
   *         {@code false} if no matching due date is found.
   */
  public boolean returnCopy(final String date) {
    boolean result = false;
    
    if (date != null && returnDates != null) {
      for (int i = 0; i < returnDates.size(); i++) {
        if (returnDates.get(i).equals(date)) {
          returnDates.remove(i);
          copiesAvailable++;
          result = true;
          break;
        }
      }
    }
    
    return result;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public List<String> getAuthors() {
    return new ArrayList<>(authors);
  }

  public void setAuthors(final List<String> authors) {
    this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
  }

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(final String language) {
    this.language = language != null ? language : "";
  }

  public String getShelvingLocation() {
    return shelvingLocation;
  }

  public void setShelvingLocation(final String shelvingLocation) {
    this.shelvingLocation = shelvingLocation != null ? shelvingLocation : "";
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(final String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(final String publisher) {
    this.publisher = publisher;
  }

  public List<String> getSubjects() {
    return new ArrayList<>(subjects);
  }

  public void setSubjects(final List<String> subjects) {
    this.subjects = subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();
  }

  public int getId() {
    return bookId;
  }

  public void setId(final int bookId) {
    this.bookId = bookId;
  }

  public int getAmountOfTimesCheckedOut() {
    return amountOfTimesCheckedOut;
  }

  public int getCopiesAvailable() {
    return copiesAvailable;
  }

  public List<String> getReturnDates() {
    return new ArrayList<>(returnDates);
  }

  public void setReturnDates(final List<String> returnDates) {
    this.returnDates = returnDates != null ? new ArrayList<>(returnDates) : new ArrayList<>();
  }

  public int getTotalCopies() {
    return totalCopies;
  }

  public void setTotalCopies(final int totalCopies) {
    this.totalCopies = totalCopies;
  }

  @Override
  public int compareTo(final Book other) {
    return Integer.compare(this.bookId, other.bookId);
  }

  @Override
  public boolean equals(final Object obj) {
    boolean result = false;
    
    if (this == obj) {
      result = true;
    } else if (obj != null && getClass() == obj.getClass()) {
      final Book cmpBook = (Book) obj;
      result = cmpBook.bookId == this.bookId;
    }
    
    return result;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(this.bookId);
  }

  @Override
  public String toString() {
    return String.format("(%d)\t%s", this.bookId, this.title);
  }
}

