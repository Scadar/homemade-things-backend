package ru.homemadethings.homemadethings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackageClasses = {
        HomemadeThingsApplication.class,
        Jsr310JpaConverters.class
})
@EnableTransactionManagement
public class HomemadeThingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomemadeThingsApplication.class, args);
    }

}
