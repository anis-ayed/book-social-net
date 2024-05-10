package com.ayed.booknetwork.book;

import com.ayed.booknetwork.common.PageResponse;
import com.ayed.booknetwork.exceptions.OperationNotPermittedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

  private final BookService bookService;

  /**
   * Add a new book to the system.
   *
   * @param book The book details to be added. Must not be null.
   * @param connectedUser The authenticated user who is adding the book.
   * @return ResponseEntity with HTTP status 201 (Created) and the ID of the newly created book.
   */
  @Operation(summary = "Add a new book")
  @ApiResponse(
      responseCode = "201",
      description = "Book created successfully",
      content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))
      })
  @PostMapping
  public ResponseEntity<Long> addNewBook(
      @Valid @RequestBody BookRequest book, Authentication connectedUser) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book, connectedUser));
  }

  /**
   * Retrieve a book by its ID.
   *
   * @param bookId The ID of the book to retrieve.
   * @return ResponseEntity with HTTP status 200 (OK) and the book details if found, or 404 (Not
   *     Found) if not found.
   * @throws EntityNotFoundException if the book entity is not fou
   */
  @Operation(summary = "Find a book by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Book found",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookResponse.class))
      })
  @ApiResponse(responseCode = "404", description = "Book not found")
  @GetMapping("{bookId}")
  public ResponseEntity<BookResponse> findBookById(@PathVariable Long bookId) {
    return ResponseEntity.ok(bookService.findBookById(bookId));
  }

  /**
   * Retrieve a paginated list of all books.
   *
   * @param page The page number (starting from 0). Default is 0.
   * @param size The size of each page. Default is 10.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and a paginated list of books.
   */
  @Operation(summary = "Find all books")
  @ApiResponse(
      responseCode = "200",
      description = "Books found",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class))
      })
  @GetMapping
  public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
      @Parameter(description = "Page number (starting from 0)", example = "0")
          @RequestParam(name = "page", defaultValue = "0", required = false)
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(name = "size", defaultValue = "10", required = false)
          int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
  }

  /**
   * Retrieve a paginated list of books owned by the authenticated user.
   *
   * @param page The page number (starting from 0). Default is 0.
   * @param size The size of each page. Default is 10.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and a paginated list of books owned by the
   *     user.
   */
  @Operation(summary = "Find all books owned by the authenticated user")
  @ApiResponse(
      responseCode = "200",
      description = "Books found",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class))
      })
  @GetMapping("/owner")
  public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
      @Parameter(description = "Page number (starting from 0)", example = "0")
          @RequestParam(name = "page", defaultValue = "0", required = false)
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(name = "size", defaultValue = "10", required = false)
          int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
  }

  /**
   * Retrieve a paginated list of borrowed books.
   *
   * @param page The page number (starting from 0). Default is 0.
   * @param size The size of each page. Default is 10.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and a paginated list of borrowed books.
   */
  @Operation(summary = "Find all borrowed books")
  @ApiResponse(
      responseCode = "200",
      description = "Borrowed books found",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class))
      })
  @GetMapping("/borrowed")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
      @Parameter(description = "Page number (starting from 0)", example = "0")
          @RequestParam(name = "page", defaultValue = "0", required = false)
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(name = "size", defaultValue = "10", required = false)
          int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
  }

  /**
   * Retrieve a paginated list of returned books.
   *
   * @param page The page number (starting from 0). Default is 0.
   * @param size The size of each page. Default is 10.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and a paginated list of returned books.
   */
  @Operation(summary = "Find all returned books")
  @ApiResponse(
      responseCode = "200",
      description = "Returned books found",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class))
      })
  @GetMapping("/returned")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
      @Parameter(description = "Page number (starting from 0)", example = "0")
          @RequestParam(name = "page", defaultValue = "0", required = false)
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(name = "size", defaultValue = "10", required = false)
          int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
  }

  /**
   * Update the shareable status of a book.
   *
   * @param bookId The ID of the book whose shareable status will be updated.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and the updated book's ID.
   * @throws OperationNotPermittedException if the operation is not permitted
   * @throws EntityNotFoundException if the book entity is not fou
   */
  @Operation(summary = "Update shareable status of a book")
  @ApiResponse(responseCode = "200", description = "Shareable status updated successfully")
  @PatchMapping("/shareable/{book-id}")
  public ResponseEntity<Long> updateShareableStatus(
      @PathVariable("book-id") Long bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
  }

  /**
   * Update the archived status of a book.
   *
   * @param bookId The ID of the book whose archived status will be updated.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and the updated book's ID.
   * @throws OperationNotPermittedException if the operation is not permitted
   * @throws EntityNotFoundException if the book entity is not fou
   */
  @Operation(summary = "Update archived status of a book")
  @ApiResponse(responseCode = "200", description = "Archived status updated successfully")
  @PatchMapping("/archived/{book-id}")
  public ResponseEntity<Long> updateArchivedStatus(
      @PathVariable("book-id") Long bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
  }

  /**
   * Borrow a book.
   *
   * @param bookId The ID of the book to borrow.
   * @param connectedUser The authenticated user.
   * @return ResponseEntity with HTTP status 200 (OK) and the ID of the borrowed book.
   * @throws OperationNotPermittedException if the operation is not permitted
   * @throws EntityNotFoundException if the book entity is not found
   */
  @Operation(summary = "Borrow a book")
  @ApiResponse(responseCode = "200", description = "Book borrowed successfully")
  @PostMapping("/borrow/{book-id}")
  public ResponseEntity<Long> borrowBook(
      @Parameter(description = "ID of the book to borrow") @PathVariable("book-id") Long bookId,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
  }
}
