package com.pos.identity.persistence

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

abstract class RepositoryBase {
    @Autowired
    protected lateinit var namedJdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate
}