package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.*;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
    public List<Member> findAll_querydsl() {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findUsername(String username) {
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }
    public List<Member> findUsername_querydsl(String username) {
        return queryFactory.selectFrom(member).where(member.username.eq(username)).fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition searchCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(searchCond.getUsername())) {
            builder.and(member.username.eq(searchCond.getUsername()));
        }
        if (searchCond.getAgeGoe() != null) {
            builder.and(member.age.goe(searchCond.getAgeGoe()));
        }
        if (searchCond.getAgeLoe() != null) {
            builder.and(member.age.loe(searchCond.getAgeLoe()));
        }
        if (hasText(searchCond.getTeamName())) {
            builder.and(team.name.eq(searchCond.getTeamName()));
        }

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> findBySearch(MemberSearchCondition searchCond) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(searchCond.getUsername()),
                        teamNameEq(searchCond.getTeamName()),
                        ageGoe(searchCond.getAgeGoe()),
                        ageLoc(searchCond.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        if (ageGoe == null) return null;
        return member.age.goe(ageGoe);
    }

    private BooleanExpression ageLoc(Integer ageLoe) {
        if (ageLoe == null) return null;
        return member.age.loe(ageLoe);
    }

    private BooleanExpression teamNameEq(String teamName) {
        if (!hasText(teamName)) return null;
        return member.team.name.eq(teamName);
    }

    private BooleanExpression usernameEq(String username) {
        if (!hasText(username)) return null;
        return member.username.eq(username);
    }

}
