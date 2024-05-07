package com.ayed.booknetwork.book;

import com.ayed.booknetwork.common.PageResponse;
import com.ayed.booknetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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

  public Long addBook(BookRequest request, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Book book = bookMapper.toBook(request);
    book.setOwner(user);
    return bookRepository.save(book).getId();
  }

  public BookResponse findBookById(Long bookId) {
    return bookRepository
        .findById(bookId)
        .map(bookMapper::toBookResponse)
        .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + bookId));
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
}
