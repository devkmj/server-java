package kr.hhplus.be.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "kr.hhplus.be.server.domain")
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	// ✅ 여기 안에 추가해야 돼!
	public CommandLineRunner checkDB(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				System.out.println("✅ DB 연결 성공: " + conn.getMetaData().getURL());
			} catch (Exception e) {
				System.out.println("❌ DB 연결 실패");
				e.printStackTrace();
			}
		};
	}

}
