package org.library.libraryback.repository;

import org.library.libraryback.dto.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByAuthorContainingIgnoreCaseOrTitleContainingIgnoreCaseOrderByIdAsc(String author, String title, Pageable pageable);
}
