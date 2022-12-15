package buckpal.kotlin.domain.ar

import buckpal.kotlin.domain.vo.Money
import java.time.LocalDateTime
import java.util.*

@JvmRecord
data class ActivityId(val value: Long)

/**
 * A money transfer activity between [Account]s
 *
 * @property ownerAccountId The account that owns this activity.
 * @property sourceAccountId The debited account.
 * @property targetAccountId The credited account.
 * @property timestamp The timestamp of the activity.
 * @property money The money that was transferred between the accounts.
 */
@JvmRecord
data class Activity(
    val id: ActivityId? = null,
    val ownerAccountId: AccountId,
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val timestamp: LocalDateTime,
    val money: Money,
) {
    constructor(
        ownerAccountId: AccountId,
        sourceAccountId: AccountId,
        targetAccountId: AccountId,
        timestamp: LocalDateTime,
        money: Money,
    ) : this(null, ownerAccountId, sourceAccountId, targetAccountId, timestamp, money)
}
