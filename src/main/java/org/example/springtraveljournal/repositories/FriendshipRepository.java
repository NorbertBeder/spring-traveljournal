package org.example.springtraveljournal.repositories;

import org.example.springtraveljournal.models.entities.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    List<Friendship> findByUserId(Long userId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
