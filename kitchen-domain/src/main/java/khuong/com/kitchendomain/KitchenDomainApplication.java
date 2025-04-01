package khuong.com.kitchendomain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
    "khuong.com.kitchendomain.entity",
    "khuong.com.smartorder_domain2.menu.entity",
    "khuong.com.smartorder_domain2.order.entity"
})
@EnableJpaRepositories(basePackages = {
    "khuong.com.kitchendomain.repository",
    "khuong.com.smartorder_domain2.menu.repository",
    "khuong.com.smartorder_domain2.order.repository"
})
public class KitchenDomainApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchenDomainApplication.class, args);
    }
}
