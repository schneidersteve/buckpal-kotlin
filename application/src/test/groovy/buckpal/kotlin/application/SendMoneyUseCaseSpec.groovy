import buckpal.kotlin.application.AccountLock
import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.application.MoneyTransferProperties
import buckpal.kotlin.application.SendMoneyCommand
import buckpal.kotlin.application.SendMoneyUseCase
import buckpal.kotlin.application.SendMoneyUseCaseImpl
import buckpal.kotlin.application.UpdateAccountStatePort
import buckpal.kotlin.domain.Account
import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money
import spock.lang.Specification

class SendMoneyUseCaseSpec extends Specification {

    LoadAccountPort loadAccountPort = Mock()
    AccountLock accountLock = Mock()
    UpdateAccountStatePort updateAccountStatePort = Mock()
    SendMoneyUseCase sendMoneyUseCase = new SendMoneyUseCaseImpl(loadAccountPort, accountLock,
            updateAccountStatePort, new MoneyTransferProperties(Money.@Companion.of(Long.MAX_VALUE)));

    def "Transaction succeeds"() {
        given: "a source account"
            Account sourceAccount = Mock()
            var sourceAccountId = new AccountId(41L)
            sourceAccount.getId() >> Optional.of(sourceAccountId)
            loadAccountPort.loadAccount(sourceAccount.getId().get(), _) >> sourceAccount
        and: "a target account"
            Account targetAccount = Mock()
            var targetAccountId = new AccountId(42L)
            targetAccount.getId() >> Optional.of(targetAccountId)
            loadAccountPort.loadAccount(targetAccount.getId().get(), _) >> targetAccount
        and:
            var money = Money.@Companion.of(500L)

        when: "money is send"
            var command = new SendMoneyCommand(
                    sourceAccount.getId().get(),
                    targetAccount.getId().get(),
                    money)
            var success = sendMoneyUseCase.sendMoney(command)

        then: "send money succeeds"
            success == true

        and: "source account is locked"
            1 * accountLock.lockAccount(sourceAccountId)
        and: "source account withdrawal will succeed"
            1 * sourceAccount.withdraw(money, targetAccountId) >> true
        and: "source account is released"
            1 * accountLock.releaseAccount(sourceAccountId)

        and: "target account is locked"
            1 * accountLock.lockAccount(targetAccountId)
        and: "target account deposit will succeed"
            1 * targetAccount.deposit(money, sourceAccountId) >> true
        and: "target account is released"
            1 * accountLock.releaseAccount(targetAccountId)

        and: "accounts have been updated"
            1 * updateAccountStatePort.updateActivities(sourceAccount)
            1 * updateAccountStatePort.updateActivities(targetAccount)
    }

    def "Given Withdrawal Fails then Only Source Account Is Locked And Released"() {
        given: "a source account"
            Account sourceAccount = Mock()
            var sourceAccountId = new AccountId(41L)
            sourceAccount.getId() >> Optional.of(sourceAccountId)
            loadAccountPort.loadAccount(sourceAccount.getId().get(), _) >> sourceAccount
        and: "a target account"
            Account targetAccount = Mock()
            var targetAccountId = new AccountId(42L)
            targetAccount.getId() >> Optional.of(targetAccountId)
            loadAccountPort.loadAccount(targetAccount.getId().get(), _) >> targetAccount
        and: "source account withdrawal will fail"
            sourceAccount.withdraw(_, _) >> false
        and: "target account deposit will succeed"
            targetAccount.deposit(_, _) >> true

        when: "money is send"
            var command = new SendMoneyCommand(
                    sourceAccount.getId().get(),
                    targetAccount.getId().get(),
                    Money.@Companion.of(300L))
            var success = sendMoneyUseCase.sendMoney(command)

        then: "send money failed"
            success == false
        and: "source account is locked"
            1 * accountLock.lockAccount(sourceAccountId)
        and: "source account is released"
            1 * accountLock.releaseAccount(sourceAccountId)
        and: "target account is not locked"
            0 * accountLock.lockAccount(targetAccountId)
    }
}
