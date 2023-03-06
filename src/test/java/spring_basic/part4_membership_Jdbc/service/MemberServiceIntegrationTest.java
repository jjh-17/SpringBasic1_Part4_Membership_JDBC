package spring_basic.part4_membership_Jdbc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring_basic.part4_membership_Jdbc.domain.Member;
import spring_basic.part4_membership_Jdbc.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
스프링 컨테이너와 DB를 연결한 통합 테스트

실제 H2-데이터베이스와 연결하여 테스트를 진행
테스트 전 DB를 비워 최적의 테스트 환경 마련 권장
 */

@SpringBootTest     //spring test
@Transactional      //테스트 후 시작 전으로 DB를 롤백
class MemberServiceIntegrationTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    //MemberRepository를 @Autowired하면 그 구현체 JdbcMemberRepository 또한 등록

    //@Commit을 추가할 경우, DB에 변경사항 저장
    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("abc");

        //When
        Long saveId = memberService.join(member);

        //Then
        Member findMember = memberRepository.findById(saveId).get();
        assertEquals(member.getName(), findMember.getName());
    }

    @Test
    public void 중복회원예외() throws Exception {
        //Given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("spring");
        member2.setName("spring");

        //When
        memberService.join(member1);

        //Then
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));//예외가 발생해야 한다.
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}
