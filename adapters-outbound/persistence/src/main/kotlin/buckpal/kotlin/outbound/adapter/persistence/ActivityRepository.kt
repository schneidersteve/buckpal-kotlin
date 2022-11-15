package buckpal.kotlin.outbound.adapter.persistence

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@R2dbcRepository(dialect = Dialect.H2)
interface ActivityRepository : CoroutineCrudRepository<ActivityEntity, Long> {

    fun findByOwnerAccountIdEqualsAndTimestampGreaterThanEquals(
        ownerAccountId: Long,
        timestamp: LocalDateTime,
    ): Flow<ActivityEntity>

    @Query(
        """
        SELECT SUM(amount) FROM activity_entity
               WHERE target_account_id = :accountId
               AND owner_account_id = :accountId
               AND timestamp < :until
        """
    )
    suspend fun getDepositBalanceUntil(accountId: Long, until: LocalDateTime): Long?

    @Query(
        "SELECT SUM(amount) FROM activity_entity " +
                "WHERE source_account_id = :accountId " +
                "AND owner_account_id = :accountId " +
                "AND timestamp < :until"
    )
    suspend fun getWithdrawalBalanceUntil(accountId: Long, until: LocalDateTime): Long?

}

@MappedEntity
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
