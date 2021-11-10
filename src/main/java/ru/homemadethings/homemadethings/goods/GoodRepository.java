package ru.homemadethings.homemadethings.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.homemadethings.homemadethings.auth.model.User;

import java.util.List;

@Repository
public interface GoodRepository extends JpaRepository<Good, Long> {
    List<Good> findAllByUser(User user);
}
