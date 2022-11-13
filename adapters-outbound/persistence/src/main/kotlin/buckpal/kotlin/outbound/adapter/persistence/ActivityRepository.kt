package buckpal.kotlin.outbound.adapter.persistence

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@R2dbcRepository(dialect = Dialect.H2)
interface ActivityRepository : CoroutineCrudRepository<ActivityEntity, Long> {

    suspend fun findByOwnerAccountIdAndTimestampGreaterThanEquals(
        ownerAccountId: Long,
        since: LocalDateTime,
    ): Flow<ActivityEntity>

    @Query(
        """
        select sum(a.amount) from ActivityEntity a
               where a.targetAccountId = :accountId
               and a.ownerAccountId = :accountId
               and a.timestamp < :until
        """
    )
    suspend fun getDepositBalanceUntil(accountId: Long, until: LocalDateTime): Long?

    @Query(
        "select sum(a.amount) from ActivityEntity a " +
                "where a.sourceAccountId = :accountId " +
                "and a.ownerAccountId = :accountId " +
                "and a.timestamp < :until"
    )
    suspend fun getWithdrawalBalanceUntil(accountId: Long, until: LocalDateTime): Long?

}

@MappedEntity
@Table(name = "activity")
data class ActivityEntity(
    @GeneratedValue
    @field:Id
    val id: Long?,
    val timestamp: LocalDateTime,
    val ownerAccountId: Long,
    val sourceAccountId: Long,
    val targetAccountId: Long,
    val amount: Long,
)
