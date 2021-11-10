package ru.homemadethings.homemadethings.good_images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodImagesRepository extends JpaRepository<GoodImage, Long> {
}
