package se.kth.journalsystem.messageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kth.journalsystem.messageservice.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderBySentAtDesc(String receiverId);

    List<Message> findBySenderIdOrderBySentAtDesc(String senderId);

    long countByReceiverIdAndIsReadFalse(String receiverId);
}
