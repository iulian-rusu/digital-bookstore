package com.pos.booklibrary.persistence

import com.pos.booklibrary.persistence.query.ParametrizedQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * Class made for working with runtime-defined queries.
 * Using queries with dynamic constraints is very tedious with JpaRepository interfaces
 * and @Query methods, so a JdbcTemplate is used instead.
 */
@Repository
class GenericQueryRepository {
    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun <T, Mapper> find(query: ParametrizedQuery, mapper: Mapper): List<T>
            where Mapper : RowMapper<T> = jdbcTemplate.query(query.getSql(), query.params, mapper)

    fun execute(query: ParametrizedQuery) =
        jdbcTemplate.update(query.getSql(), query.params)
}