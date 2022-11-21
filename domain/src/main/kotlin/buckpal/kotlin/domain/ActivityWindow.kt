package buckpal.kotlin.domain

import java.time.LocalDateTime
import java.util.*

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

/**
 * A window of account activities.
 *
 * @property activities The list of account activities within this window.
 */
class ActivityWindow(val activities: MutableList<Activity>) {

    constructor(vararg activities: Activity) : this(mutableListOf(*activities))

    /**
     * The timestamp of the first activity within this window.
     */
    fun getStartTimestamp(): LocalDateTime {
        return activities.stream().min(Comparator.comparing(Activity::timestamp))
            .orElseThrow { IllegalStateException() }.timestamp
    }

    /**
     * The timestamp of the last activity within this window.
     * @return
     */
    fun getEndTimestamp(): LocalDateTime {
        return activities.stream().max(Comparator.comparing(Activity::timestamp))
            .orElseThrow { IllegalStateException() }.timestamp
    }

    /**
     * Calculates the balance by summing up the values of all activities within this window.
     */
    fun calculateBalance(accountId: AccountId): Money {
        val depositBalance = activities
            .filter { it.targetAccountId == accountId }
            .map { it.money }
            .reduceOrNull { acc, money -> Money.add(acc, money) } ?: Money.ZERO
        val withdrawalBalance: Money =
            activities.stream()
                .filter { a: Activity -> a.sourceAccountId == accountId }
                .map(Activity::money)
                .reduce(Money.ZERO, Money::add)
        return Money.add(depositBalance, withdrawalBalance.negate())
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }
}
