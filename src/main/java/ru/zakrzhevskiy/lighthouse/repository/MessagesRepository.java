package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.Message;

import java.util.Optional;

public interface MessagesRepository extends JpaRepository<Message, Long> {

    Optional<Page<Message>> findByOrderId(Long id, Pageable pageable);

}
