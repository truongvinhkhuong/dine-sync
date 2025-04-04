package khuong.com.kitchendomain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
    "khuong.com.kitchendomain.entity"
})
@EnableJpaRepositories(basePackages = {
    "khuong.com.kitchendomain.repository"
})
public class KitchenDomainApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchenDomainApplication.class, args);
    }
}
