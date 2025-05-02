package io.github.hs96wings.streaming_server.member.repository;

import io.github.hs96wings.streaming_server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserid(String userid);
}
