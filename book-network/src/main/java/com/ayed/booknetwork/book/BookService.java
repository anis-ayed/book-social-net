package com.ayed.booknetwork.book;

import static com.ayed.booknetwork.book.BookSpecification.withOwnerId;

import com.ayed.booknetwork.common.PageResponse;
import com.ayed.booknetwork.exceptions.OperationNotPermittedException;
import com.ayed.booknetwork.history.BookTransactionHistory;
import com.ayed.booknetwork.history.BookTransactionHistoryRepository;
import com.ayed.booknetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

  public Long addBook(BookRequest request, Authentication connectedUser) {

    User user = ((User) connectedUser.getPrincipal());
    Book book = bookMapper.toBook(request);
    book.setOwner(user);
    return bookRepository.save(book).getId();
  }

  public BookResponse findBookById(Long bookId) {
    return bookMapper.toBookResponse(getBookById(bookId));
  }

  public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
    List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
    return new PageResponse<>(
        bookResponses,
        books.getNumber(),
        books.getSize(),
        books.getTotalElements(),
        books.getTotalPages(),
        books.isFirst(),
        books.isLast());
  }

  public PageResponse<BookResponse> findAllBooksByOwner(
      int page, int size, Authentication connectedUser) {

    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
    List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
    return new PageResponse<>(
        bookResponses,
        books.getNumber(),
        books.getSize(),
        books.getTotalElements(),
        books.getTotalPages(),
        books.isFirst(),
        books.isLast());
  }

  public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(
      int page, int size, Authentication connectedUser) {

    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBorrowedBooks =
        bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
    List<BorrowedBookResponse> bookResponses =
        allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
    return new PageResponse<>(
        bookResponses,
        allBorrowedBooks.getNumber(),
        allBorrowedBooks.getSize(),
        allBorrowedBooks.getTotalElements(),
        allBorrowedBooks.getTotalPages(),
        allBorrowedBooks.isFirst(),
        allBorrowedBooks.isLast());
  }

  public PageResponse<BorrowedBookResponse> findAllReturnedBooks(
      int page, int size, Authentication connectedUser) {

    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBorrowedBooks =
        bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
    List<BorrowedBookResponse> bookResponses =
        allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
    return new PageResponse<>(
        bookResponses,
        allBorrowedBooks.getNumber(),
        allBorrowedBooks.getSize(),
        allBorrowedBooks.getTotalElements(),
        allBorrowedBooks.getTotalPages(),
        allBorrowedBooks.isFirst(),
        allBorrowedBooks.isLast());
  }

  public Long updateShareableStatus(Long bookId, Authentication connectedUser) {

    Book book = getBookById(bookId);
    User user = ((User) connectedUser.getPrincipal());
    if (!isUserTheOwnerOfThisBook(book, user)) {
      throw new OperationNotPermittedException("You cannot update books shareable status");
    }
    book.setShareable(!book.isShareable());
    bookRepository.save(book);
    return bookId;
  }

  public Long updateArchivedStatus(Long bookId, Authentication connectedUser) {

    Book book = getBookById(bookId);
    User user = ((User) connectedUser.getPrincipal());
    if (!Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot update books archived status");
    }
    book.setArchived(!book.isArchived());
    bookRepository.save(book);
    return bookId;
  }

  public Long borrowBook(Long bookId, Authentication connectedUser) {

    Book book = getBookById(bookId);
    if (book.isArchived() || !book.isShareable()) {
      throw new OperationNotPermittedException("The requested book cannot be borrowed!");
    }
    User user = ((User) connectedUser.getPrincipal());
    if (isUserTheOwnerOfThisBook(book, user)) {
      throw new OperationNotPermittedException("You cannot borrow your own books");
    }
    final boolean isAlreadyBorrowed =
        bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
    if (isAlreadyBorrowed) {
      throw new OperationNotPermittedException("The requested book is already borrowed!");
    }
    BookTransactionHistory bookTransactionHistory =
        BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returnApproved(false)
            .build();
    return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
  }

  private boolean isUserTheOwnerOfThisBook(Book book, User user) {
    return Objects.equals(book.getOwner().getId(), user.getId());
  }

  private Book getBookById(Long bookId) {
    return bookRepository
        .findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
  }
}
