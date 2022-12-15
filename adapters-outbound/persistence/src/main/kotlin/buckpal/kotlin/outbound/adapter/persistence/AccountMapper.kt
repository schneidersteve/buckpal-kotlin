package buckpal.kotlin.outbound.adapter.persistence

import buckpal.kotlin.domain.ar.Account
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.ar.Activity
import buckpal.kotlin.domain.ar.ActivityId
import buckpal.kotlin.domain.vo.ActivityWindow
import buckpal.kotlin.domain.vo.Money
import jakarta.inject.Singleton

@Singleton
class AccountMapper {

    fun mapToAccount(
        account: AccountEntity,
        activities: List<ActivityEntity>,
        withdrawalBalance: Long,
        depositBalance: Long,
    ): Account {
        val baselineBalance = Money.subtract(Money.of(depositBalance), Money.of(withdrawalBalance))
        return Account.withId(AccountId(account.id!!), baselineBalance, mapToActivityWindow(activities))
    }

    private fun mapToActivityWindow(activities: List<ActivityEntity>): ActivityWindow {
        return ActivityWindow(activities.map {
            Activity(
                ActivityId(it.id!!),
                AccountId(it.ownerAccountId),
                AccountId(it.sourceAccountId),
                AccountId(it.targetAccountId),
                it.timestamp,
                Money.of(it.amount)
            )
        }.toMutableList())
    }

    fun mapToActivityEntity(activity: Activity): ActivityEntity {
        return ActivityEntity(
            activity.id?.value,
            activity.timestamp,
            activity.ownerAccountId.value,
            activity.sourceAccountId.value,
            activity.targetAccountId.value,
            activity.money.amount.toLong()
        )
    }

}
