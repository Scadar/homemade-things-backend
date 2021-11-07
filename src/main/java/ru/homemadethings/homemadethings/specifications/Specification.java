package ru.homemadethings.homemadethings.specifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.homemadethings.homemadethings.goods.Good;

import javax.persistence.*;

@Table(name = "specification")
@Entity
@Data
public class Specification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "specification_seq")
    @SequenceGenerator(name = "specification_seq", allocationSize = 1, initialValue = 501)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "value", nullable = false)
    private String value;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "good_id")
    private Good good;

}