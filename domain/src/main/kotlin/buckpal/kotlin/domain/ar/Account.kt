package buckpal.kotlin.domain.ar

import buckpal.kotlin.domain.vo.ActivityWindow
import buckpal.kotlin.domain.vo.Money
import java.time.LocalDateTime
import java.util.*

@JvmRecord
data class AccountId(val value: Long)

/**
 * An account that holds a certain amount of money. An [Account] object only
 * contains a window of the latest account activities. The total balance of the account is
 * the sum of a baseline balance that was valid before the first activity in the
 * window and the sum of the activity values.
 *
 * @property id The unique ID of the account.
 * @property baselineBalance The baseline balance of the account. This was the balance of the account before the first activity in the activityWindow.
 * @property activityWindow The window of latest activities on this account.
 */
// open for mocking
open class Account(private val id: AccountId?, val baselineBalance: Money, val activityWindow: ActivityWindow) {

    // open for mocking
    open fun getId(): Optional<AccountId> {
        return Optional.ofNullable(id)
    }

    /**
     * Calculates the total balance of the account by adding the activity values to the baseline balance.
     */
    fun calculateBalance(): Money {
        return Money.add(
            baselineBalance, activityWindow.calculateBalance(id!!)
        )
    }

    /**
     * Tries to withdraw a certain amount of money from this account.
     * If successful, creates a new activity with a negative value.
     * @return true if the withdrawal was successful, false if not.
     */
    // open for mocking
    open fun withdraw(money: Money, targetAccountId: AccountId): Boolean {
        if (!mayWithdraw(money)) {
            return false
        }
        val withdrawal = Activity(
            id!!, id, targetAccountId, LocalDateTime.now(), money
        )
        activityWindow.addActivity(withdrawal)
        return true
    }

    private fun mayWithdraw(money: Money): Boolean {
        return Money.add(
            calculateBalance(), money.negate()
        ).isPositiveOrZero()
    }

    /**
     * Tries to deposit a certain amount of money to this account.
     * If sucessful, creates a new activity with a positive value.
     * @return true if the deposit was successful, false if not.
     */
    // open for mocking
    open fun deposit(money: Money, sourceAccountId: AccountId): Boolean {
        val deposit = Activity(
            id!!, sourceAccountId, id, LocalDateTime.now(), money
        )
        activityWindow.addActivity(deposit)
        return true
    }

    companion object {
        /**
         * Creates an [Account] entity without an ID. Use to create a new entity that is not yet
         * persisted.
         */
        fun withoutId(
            baselineBalance: Money,
            activityWindow: ActivityWindow,
        ): Account {
            return Account(null, baselineBalance, activityWindow)
        }

        /**
         * Creates an [Account] entity with an ID. Use to reconstitute a persisted entity.
         */
        fun withId(
            accountId: AccountId,
            baselineBalance: Money,
            activityWindow: ActivityWindow,
        ): Account {
            return Account(accountId, baselineBalance, activityWindow)
        }
    }
}