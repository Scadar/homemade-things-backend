package ru.homemadethings.homemadethings.good_images;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.homemadethings.homemadethings.goods.Good;

import javax.persistence.*;

@Table(name = "good_images")
@Entity
@Data
public class GoodImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goods_images_seq")
    @SequenceGenerator(name = "goods_images_seq", allocationSize = 1)
    private Long id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "good_id")
    @JsonIgnore
    private Good good;
}