package buckpal.kotlin.outbound.adapter.persistence

import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.application.UpdateAccountStatePort
import buckpal.kotlin.domain.ar.Account
import buckpal.kotlin.domain.ar.AccountId
import jakarta.inject.Singleton
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
internal class AccountPersistenceAdapter(
    private val accountRepository: AccountRepository,
    private val activityRepository: ActivityRepository,
    private val accountMapper: AccountMapper,
) : LoadAccountPort,
    UpdateAccountStatePort {

    private val logger: Logger = LoggerFactory.getLogger(AccountPersistenceAdapter::class.java)

    override suspend fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime,
    ): Account {
        val account = accountRepository.findById(accountId.value) ?: throw EntityNotFoundException()
        logger.debug("findById(id = $accountId) = $account");

        val activities =
            activityRepository.findByOwnerAccountIdEqualsAndTimestampGreaterThanEquals(accountId.value, baselineDate)
        logger.debug("findByOwnerAccountIdEqualsAndTimestampGreaterThanEquals(ownerAccountId = $accountId, timestamp = $baselineDate) = ${activities.toList()}");

        val withdrawalBalance = activityRepository.getWithdrawalBalanceUntil(accountId.value, baselineDate) ?: 0L
        logger.debug("getWithdrawalBalanceUntil(accountId = $accountId, until = $baselineDate) = $withdrawalBalance");

        val depositBalance = activityRepository.getDepositBalanceUntil(accountId.value, baselineDate) ?: 0L
        logger.debug("getDepositBalanceUntil(accountId = $accountId, until = $baselineDate) = $depositBalance");

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
                val ae = accountMapper.mapToActivityEntity(activity)
                logger.debug("save(entity = $ae)");
                activityRepository.save(ae)
            }
        }
    }
}
