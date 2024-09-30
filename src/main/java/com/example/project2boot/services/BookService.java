package com.example.project2boot.services;


import com.example.project2boot.models.Book;
import com.example.project2boot.models.Person;
import com.example.project2boot.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        return bookRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setOwner(selectedPerson);
            book.setTakenAt(LocalDateTime.now());
            bookRepository.save(book);

        }
    }

    @Transactional
    public void release(int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setOwner(null);
            book.setHandOver(LocalDateTime.now());
            if(isOverdue(book.getId())) {
                System.out.println("Book is overdue");
            } else {
                System.out.println("all right");
            }
            bookRepository.save(book);
        }
    }

    public List<Book> sortedByYear() {
        return bookRepository.findAllByOrderByYearAsc();
    }


    public Boolean isOverdue(int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            LocalDateTime takenAt = book.getTakenAt();
            LocalDateTime handOver = book.getHandOver();
            if (takenAt != null && handOver != null) {
                if (Duration.between(takenAt, handOver).toDays() > 14) {
                    return true;
                } else {
                    return false;
                }
            }


        }
        return false;
    }

    public Optional<Book> findBookByTitle(String title) {
        return bookRepository.findByTitleStartingWith(title);
    }

}
