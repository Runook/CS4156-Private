# Bug Report

## Compilation Errors (Fixed in Part 0)

1. **Compilation Error: RouteController.java:[104,3] missing return statement**
   - **Cause**: Missing return statement
   - **Fix**: Added a default `return ResponseEntity.notFound().build();` after 104"}"

2. **Compilation Error: Book.java:[268,20] incompatible types: java.lang.Object cannot be converted** to dev.coms4156.project.individualproject.model.Book
   - **Cause**: obj is Object, Compiler can't allow "Object = Book"
   - **Fix**: Book cmpBook = (Book) obj;

3. **Compilation Error: Book.java:[183,3] missing return statement**
   - **Cause**: missing return statement
   - **Fix**: return this.language;

## Logic Errors (Fixed in Part 3 - Bug Fixing)

### Critical Logic Bugs Discovered Through Test Failures:

4. **Bug: deleteCopy() method returns wrong boolean values**
   - **Location**: Book.java:114-116
   - **Issue**: Method returned `false` when deletion was successful and `true` when deletion failed (completely inverted logic)
   - **Test Failure**: `BookTest.hasCopies_thenDelete_toFalse` - Expected successful deletion to return `true`
   - **Fix**: Corrected return values - now returns `true` for successful deletion, `false` for failure
   - **Root Cause**: Inverted boolean logic

5. **Bug: addCopy() method was completely empty**
   - **Location**: Book.java:119-121
   - **Issue**: Method body was empty - no functionality implemented
   - **Test Failures**: `BookTest.addCopy_increasesBothCounters`, `RouteControllerTest.addCopy_success_and_notFound418`
   - **Fix**: Implemented proper functionality: `totalCopies++; copiesAvailable++;`
   - **Root Cause**: Method not implemented (empty body)

6. **Bug: checkoutCopy() decremented counter instead of incrementing**
   - **Location**: Book.java:133
   - **Issue**: `amountOfTimesCheckedOut--` should be `amountOfTimesCheckedOut++`
   - **Test Failure**: `BookTest.checkoutCopy_success_then_null_when_empty` - Expected checkout counter to increase
   - **Fix**: Changed decrement to increment operation
   - **Root Cause**: Wrong arithmetic operator

7. **Bug: returnCopy() had inverted isEmpty() logic**
   - **Location**: Book.java:152
   - **Issue**: Method checked `if (returnDates.isEmpty())` but then tried to iterate through the empty list
   - **Test Failure**: `BookTest.returnCopy_allBranches` - Return operations failed
   - **Fix**: Added proper null checking and removed the inverted isEmpty check
   - **Root Cause**: Inverted conditional logic

8. **Bug: setShelvingLocation() set literal string instead of parameter**
   - **Location**: Book.java:195
   - **Issue**: `this.shelvingLocation = "shelvingLocation";` instead of `this.shelvingLocation = shelvingLocation;`
   - **Test Failure**: Various setter tests failed for null handling
   - **Fix**: Use parameter value and add null protection: `this.shelvingLocation = shelvingLocation != null ? shelvingLocation : "";`
   - **Root Cause**: Used string literal instead of parameter

9. **Bug: hasCopies() used wrong comparison operator**
   - **Location**: Book.java:96
   - **Issue**: `return copiesAvailable >= 0;` should be `return copiesAvailable > 0;`
   - **Test Failure**: `BookTest.hasCopies_thenDelete_toFalse` - Method returned true when no copies available
   - **Fix**: Changed from `>= 0` to `> 0`
   - **Root Cause**: Wrong comparison operator

10. **Bug: Missing hashCode() method implementation**
    - **Location**: Book.java - method was completely missing
    - **Issue**: Class implemented equals() but not hashCode(), violating Java contract
    - **Test Failure**: `BookTest.equals_hash_compareTo` - Hash codes didn't match for equal objects
    - **Fix**: Added `@Override public int hashCode() { return Integer.hashCode(this.id); }`
    - **Root Cause**: Incomplete equals/hashCode implementation

### Null Handling Bugs in Setter Methods:

11. **Bug: Multiple setter methods lacked null protection**
    - **Locations**: Book.java setAuthors(), setLanguage(), setSubjects()
    - **Issue**: Methods didn't handle null input parameters properly
    - **Test Failure**: `BookTest.setters_nullBranches_defaultsApplied`
    - **Fix**: Added null checks with default values:
      - `setAuthors()`: `this.authors = authors != null ? authors : new ArrayList<>();`
      - `setLanguage()`: `this.language = language != null ? language : "";`
      - `setSubjects()`: `this.subjects = subjects != null ? subjects : new ArrayList<>();`
    - **Root Cause**: Missing null parameter validation

### Static Analysis Issues (Found by PMD):

12. **Bug: Unused local variable in RouteController**
    - **Location**: RouteController.java:94
    - **Issue**: `StringBuilder currBookId = new StringBuilder(book.getId());` was unused
    - **PMD Warning**: "Avoid unused local variables such as 'currBookId'"
    - **Fix**: Removed the unused variable declaration
    - **Root Cause**: Unnecessary code/dead code

## Summary

- **Total Bugs Fixed**: 12 bugs
- **Critical Logic Errors**: 8 bugs  
- **Compilation Errors**: 3 bugs
- **Code Quality Issues**: 1 bug
- **Test Success Rate**: Improved from 50% (7/14 failing) to 100% (14/14 passing)
- **Static Analysis**: Reduced PMD warnings from 25+ to manageable levels

## Testing Strategy

1. **Unit Tests**: Used existing JUnit test suite to identify functional bugs
2. **Static Analysis**: Used PMD to identify code quality and logic issues  
3. **Manual Code Review**: Compared method implementations against Javadoc specifications
4. **Integration Testing**: Verified controller endpoints work correctly with fixed business logic

## Tools Used

- **PMD 7.16.0**: Static code analysis for bug detection
- **JUnit 5**: Unit testing framework for functional verification
- **Maven Surefire**: Test execution and reporting
- **Checkstyle**: Code style and formatting verification