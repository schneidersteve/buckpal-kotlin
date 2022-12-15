package buckpal.kotlin.domain.vo

import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.ar.Activity
import java.time.LocalDateTime
import java.util.Comparator

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
            .fold(Money.ZERO) { acc, money -> Money.add(acc, money) }
        val withdrawalBalance: Money =
            activities.stream()
                .filter { a: Activity -> a.sourceAccountId == accountId }
                .map(Activity::money)
                .reduce(Money.ZERO, Money.Companion::add)
        return Money.add(depositBalance, withdrawalBalance.negate())
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }
}
