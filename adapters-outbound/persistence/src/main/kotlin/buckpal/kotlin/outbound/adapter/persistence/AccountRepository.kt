package buckpal.kotlin.outbound.adapter.persistence

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@R2dbcRepository(dialect = Dialect.H2)
interface AccountRepository : CoroutineCrudRepository<AccountEntity, Long> {
}

@MappedEntity
data class AccountEntity(
    @GeneratedValue
    @field:Id
    val id: Long?,
)
