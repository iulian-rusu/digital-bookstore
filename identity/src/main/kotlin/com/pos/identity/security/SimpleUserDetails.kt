package com.pos.identity.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SimpleUserDetails(
    val userId: Long,
    val role: String,
    private val username: String,
) : UserDetails {
    override fun getAuthorities() = mutableListOf(SimpleGrantedAuthority(role))
    override fun getPassword() = ""
    override fun getUsername() = username
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}