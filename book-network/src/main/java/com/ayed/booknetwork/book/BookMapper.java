package com.ayed.booknetwork.book;

import com.ayed.booknetwork.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
  public Book toBook(BookRequest request) {
    return Book.builder()
        .id(request.id())
        .title(request.title())
        .authorName(request.authorName())
        .synopsis(request.synopsis())
        .archived(false)
        .shareable(request.shareable())
        .build();
  }

  public BookResponse toBookResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .authorName(book.getAuthorName())
        .isbn(book.getIsbn())
        .synopsis(book.getSynopsis())
        .archived(book.isArchived())
        .shareable(book.isShareable())
        .rate(book.getRate())
        .owner(book.getOwner().fullName())
        .build();
  }

  public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
    return BorrowedBookResponse.builder()
        .id(history.getBook().getId())
        .title(history.getBook().getTitle())
        .authorName(history.getBook().getAuthorName())
        .returned(history.isReturned())
        .returnApproved(history.isReturnApproved())
        .rate(history.getBook().getRate())
        .build();
  }
}
