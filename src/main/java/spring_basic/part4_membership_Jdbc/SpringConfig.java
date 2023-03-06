package spring_basic.part4_membership_Jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring_basic.part4_membership_Jdbc.repository.JdbcMemberRepository;
import spring_basic.part4_membership_Jdbc.repository.MemberRepository;
import spring_basic.part4_membership_Jdbc.service.MemberService;
import javax.sql.DataSource;

@Configuration
public class SpringConfig {
    //변수
    private final DataSource dataSource;

    //생성자
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //스프링 빈 등록
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new JdbcMemberRepository(dataSource);
    }
}
