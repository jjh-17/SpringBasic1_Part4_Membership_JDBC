package spring_basic.part4_membership_Jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Part4MembershipJdbcApplication {

	/*
	순수 Jdbc : 거의 사용하지 않으므로, 참고용
	스프링 컨테이너와 H2-데이터베이스를 이용한 회원 관리
	 */
	public static void main(String[] args) {
		SpringApplication.run(Part4MembershipJdbcApplication.class, args);
	}

}
