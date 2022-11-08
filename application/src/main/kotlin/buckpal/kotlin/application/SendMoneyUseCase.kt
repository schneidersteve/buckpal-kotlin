package buckpal.kotlin.application

import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money
import java.time.LocalDateTime
import javax.transaction.Transactional

@Transactional
class SendMoneyUseCaseImpl(
    val loadAccountPort: LoadAccountPort,
    val accountLock: AccountLock,
    val updateAccountStatePort: UpdateAccountStatePort,
    val moneyTransferProperties: MoneyTransferProperties,
) : SendMoneyUseCase {
    override fun sendMoney(command: SendMoneyCommand): Boolean {

        checkThreshold(command)

        val baselineDate = LocalDateTime.now().minusDays(10)

        val sourceAccount = loadAccountPort.loadAccount(
            command.sourceAccountId,
            baselineDate
        )

        val targetAccount = loadAccountPort.loadAccount(
            command.targetAccountId,
            baselineDate
        )

        val sourceAccountId: AccountId = sourceAccount.getId()
            .orElseThrow { IllegalStateException("expected source account ID not to be empty") }
        val targetAccountId: AccountId = targetAccount.getId()
            .orElseThrow { IllegalStateException("expected target account ID not to be empty") }

        accountLock.lockAccount(sourceAccountId)
        if (!sourceAccount.withdraw(command.money, targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId)
            return false
        }

        accountLock.lockAccount(targetAccountId)
        if (!targetAccount.deposit(command.money, sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId)
            accountLock.releaseAccount(targetAccountId)
            return false
        }

        updateAccountStatePort.updateActivities(sourceAccount)
        updateAccountStatePort.updateActivities(targetAccount)

        accountLock.releaseAccount(sourceAccountId)
        accountLock.releaseAccount(targetAccountId)
        return true
    }

    private fun checkThreshold(command: SendMoneyCommand) {
        if (command.money.isGreaterThan(moneyTransferProperties.maximumTransferThreshold)) {
            throw ThresholdExceededException(moneyTransferProperties.maximumTransferThreshold, command.money)
        }
    }
}

data class MoneyTransferProperties(var maximumTransferThreshold: Money = Money.of(1_000_000L))

class ThresholdExceededException(threshold: Money, actual: Money) : RuntimeException(
    String.format(
        "Maximum threshold for transferring money exceeded: tried to transfer %s but threshold is %s!",
        actual,
        threshold
    )
)
