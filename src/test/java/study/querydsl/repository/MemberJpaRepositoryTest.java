package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member member1 = memberJpaRepository.findById(member.getId()).get();
        System.out.println("member1 = " + member1);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).containsExactly(member);

        List<Member> findMember1 = memberJpaRepository.findUsername(member.getUsername());
        assertThat(findMember1).containsExactly(member);

    }

    @Test
    public void basicQuerydslTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        List<Member> result1 = memberJpaRepository.findAll_querydsl();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findUsername_querydsl(member.getUsername());
        assertThat(result2).containsExactly(member);

    }

    @Test
    public void searchByBuilder() {

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

        List<MemberTeamDto> members = memberJpaRepository.searchByBuilder(searchCond);
        for (MemberTeamDto member : members) {
            System.out.println("member = " + member);
        }
        assertThat(members).extracting("username").containsExactly("member4");
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

        List<MemberTeamDto> members = memberJpaRepository.findBySearch(searchCond);
        for (MemberTeamDto member : members) {
            System.out.println("member = " + member);
        }
        assertThat(members).extracting("username").containsExactly("member4");
    }

}