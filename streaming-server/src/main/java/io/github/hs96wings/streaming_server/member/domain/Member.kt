package io.github.hs96wings.streaming_server.member.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
open class Member @JvmOverloads constructor (
    @Column(nullable = false, unique = true)
    var userid: String,
    @Column(name = "password")
    var pwd: String,
    // EnumType.STRING은 그대로 유지, 기본값 설정으로 @Builder.Default 대체
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
): UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // --- UserDetails 인터페이스 메서드 오버라이드 ---
    override fun getAuthorities(): Collection<GrantedAuthority> {
        // "ROLE_" 접두사와 enum의 이름을 조합하여 권한 생성
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getUsername(): String {
        // UserDetails의 getUsername()이 userid를 반환하도록 오버라이드
        return this.userid
    }

    override fun getPassword(): String {
        return this.pwd
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
    constructor(id: Long, userid: String, role: Role) : this(userid, "", role) {
        this.id = id
    }
}