package ru.netology.diplomcloud.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplomcloud.entity.Cloud;
import ru.netology.diplomcloud.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CloudRepository extends JpaRepository<Cloud, Long> {
    List<Cloud> findAllByUser(User user, Limit limit);

    Optional<Cloud> findByUserAndFileName(User user, String fileName);

    void deleteByUserAndFileName(User user, String fileName);
}
