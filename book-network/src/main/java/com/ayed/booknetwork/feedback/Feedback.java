package com.ayed.booknetwork.feedback;

import com.ayed.booknetwork.book.Book;
import com.ayed.booknetwork.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Feedback extends BaseEntity {
  private Double note;
  private String comment;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;
}
