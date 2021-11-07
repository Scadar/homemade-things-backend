package ru.homemadethings.homemadethings.goods;

import lombok.Data;
import ru.homemadethings.homemadethings.specifications.SpecificationRequest;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodRequest {

    private final String title;

    private final BigDecimal price;

    private final Integer discount;

    private final String description;

    private final List<String> specifications;

}
