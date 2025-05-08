package kr.hhplus.be.server;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.sql.DataSource;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "kr.hhplus.be.server.infrastructure")
@EntityScan(basePackages = "kr.hhplus.be.server.domain")
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(DataSource dataSource) {
		return args -> {
			System.out.println("🎯 JDBC URL = " + dataSource.getConnection().getMetaData().getURL());
		};
	}

}
