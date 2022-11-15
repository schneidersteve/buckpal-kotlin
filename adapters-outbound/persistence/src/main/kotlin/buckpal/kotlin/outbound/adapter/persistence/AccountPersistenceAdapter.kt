package buckpal.kotlin.outbound.adapter.persistence

import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.application.UpdateAccountStatePort
import buckpal.kotlin.domain.Account
import buckpal.kotlin.domain.AccountId
import jakarta.inject.Singleton
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.flow.toList
import java.time.LocalDateTime

@Singleton
class AccountPersistenceAdapter(
    private val accountRepository: AccountRepository,
    private val activityRepository: ActivityRepository,
    private val accountMapper: AccountMapper,
) : LoadAccountPort,
    UpdateAccountStatePort {

    override suspend fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime,
    ): Account {
        val account = accountRepository.findById(accountId.value) ?: throw EntityNotFoundException()

        val activities = activityRepository.findByOwnerAccountIdEqualsAndTimestampGreaterThanEquals(accountId.value, baselineDate)

        val withdrawalBalance = activityRepository.getWithdrawalBalanceUntil(accountId.value, baselineDate) ?: 0L

        val depositBalance = activityRepository.getDepositBalanceUntil(accountId.value, baselineDate) ?: 0L

        return accountMapper.mapToAccount(
            account,
            activities.toList(),
            withdrawalBalance,
            depositBalance
        )
    }

    override suspend fun updateActivities(account: Account) {
        account.activityWindow.activities.forEach { activity ->
            if (activity.id == null) {
                activityRepository.save(accountMapper.mapToActivityEntity(activity))
            }
        }
    }
}
