package ru.homemadethings.homemadethings.goods;


import lombok.Data;
import ru.homemadethings.homemadethings.specifications.Specification;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Table(name = "good")
@Entity
@Data
public class Good {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goods_seq")
    @SequenceGenerator(name = "goods_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private Integer discount;

    @Column(length = 25500)
    @NotNull(message = "Description is required")
    private String description;

    @OneToMany(mappedBy = "good", orphanRemoval = true)
    private List<Specification> specifications;

}
