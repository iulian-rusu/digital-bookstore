package com.pos.identity.persistence

import com.pos.identity.persistence.query.ParametrizedQuery
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class GenericQueryRepository : RepositoryBase() {
    fun execute(query: ParametrizedQuery) =
        namedJdbcTemplate.update(query.getSql(), query.params)
}