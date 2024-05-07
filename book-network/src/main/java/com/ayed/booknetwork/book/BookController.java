package com.ayed.booknetwork.book;

import com.ayed.booknetwork.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
  }
}
