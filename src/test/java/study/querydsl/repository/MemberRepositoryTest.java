package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member member1 = memberRepository.findById(member.getId()).get();
        System.out.println("member1 = " + member1);

        List<Member> all = memberRepository.findAll();
        assertThat(all).containsExactly(member);

        List<Member> findMember1 = memberRepository.findByUsername(member.getUsername());
        assertThat(findMember1).containsExactly(member);

    }

    @Test
    public void searchByParameter() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition searchCond = new MemberSearchCondition();
        searchCond.setUsername("member4");
//        searchCond.setTeamName("teamB");
        searchCond.setAgeGoe(35);
//        searchCond.setAgeLoe(40);

        List<MemberTeamDto> members = memberRepository.search(searchCond);
        for (MemberTeamDto member : members) {
            System.out.println("member = " + member);
        }
        assertThat(members).extracting("username").containsExactly("member4");
    }

    @Test
    public void search() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition searchCond = new MemberSearchCondition();
//        searchCond.setUsername("member4");
//        searchCond.setTeamName("teamB");
//        searchCond.setAgeGoe(35);
//        searchCond.setAgeLoe(40);

        PageRequest page = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(searchCond , page);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3");

    }
}