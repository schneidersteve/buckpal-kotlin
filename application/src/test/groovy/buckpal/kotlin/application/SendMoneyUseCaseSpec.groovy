import buckpal.kotlin.application.AccountLock
import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.application.c.MoneyTransferProperties
import buckpal.kotlin.application.SendMoneyCommand
import buckpal.kotlin.application.SendMoneyUseCase
import buckpal.kotlin.application.c.SendMoneyUseCaseImpl
import buckpal.kotlin.application.UpdateAccountStatePort
import buckpal.kotlin.domain.ar.Account
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.Money
import kotlin.coroutines.Continuation
import kotlinx.coroutines.Dispatchers
import spock.lang.Specification

class SendMoneyUseCaseSpec extends Specification {

    LoadAccountPort loadAccountPort = Mock()

    AccountLock accountLock = Mock()

    UpdateAccountStatePort updateAccountStatePort = Mock()

    SendMoneyUseCase sendMoneyUseCase = new SendMoneyUseCaseImpl(loadAccountPort, accountLock,
            updateAccountStatePort, new MoneyTransferProperties(Money.@Companion.of(Long.MAX_VALUE)));

    // Kotlin suspend function parameter
    var continuation = Mock(Continuation) {
        getContext() >> Dispatchers.Default
    }

    def "Transaction succeeds"() {
        given: "a source account"
            Account sourceAccount = Mock()
            var sourceAccountId = new AccountId(41L)
            sourceAccount.getId() >> Optional.of(sourceAccountId)
            // Kotlin suspend function is extended by one more parameter at compile time
            loadAccountPort.loadAccount(sourceAccount.getId().get(), _, _) >> sourceAccount
        and: "a target account"
            Account targetAccount = Mock()
            var targetAccountId = new AccountId(42L)
            targetAccount.getId() >> Optional.of(targetAccountId)
            // Kotlin suspend function is extended by one more parameter at compile time
            loadAccountPort.loadAccount(targetAccount.getId().get(), _, _) >> targetAccount
        and:
            var money = Money.@Companion.of(500L)

        when: "money is send"
            var command = new SendMoneyCommand(
                    sourceAccount.getId().get(),
                    targetAccount.getId().get(),
                    money)
            // Kotlin suspend function is extended by one more parameter at compile time
            var success = sendMoneyUseCase.sendMoney(command, continuation)

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
            // Kotlin suspend function is extended by one more parameter at compile time
            1 * updateAccountStatePort.updateActivities(sourceAccount, _)
            1 * updateAccountStatePort.updateActivities(targetAccount, _)
    }

    def "Given Withdrawal Fails then Only Source Account Is Locked And Released"() {
        given: "a source account"
            Account sourceAccount = Mock()
            var sourceAccountId = new AccountId(41L)
            sourceAccount.getId() >> Optional.of(sourceAccountId)
            // Kotlin suspend function is extended by one more parameter at compile time
            loadAccountPort.loadAccount(sourceAccount.getId().get(), _, _) >> sourceAccount
        and: "a target account"
            Account targetAccount = Mock()
            var targetAccountId = new AccountId(42L)
            targetAccount.getId() >> Optional.of(targetAccountId)
            // Kotlin suspend function is extended by one more parameter at compile time
            loadAccountPort.loadAccount(targetAccount.getId().get(), _, _) >> targetAccount
        and: "source account withdrawal will fail"
            sourceAccount.withdraw(_, _) >> false
        and: "target account deposit will succeed"
            targetAccount.deposit(_, _) >> true

        when: "money is send"
            var command = new SendMoneyCommand(
                    sourceAccount.getId().get(),
                    targetAccount.getId().get(),
                    Money.@Companion.of(300L))
            // Kotlin suspend function is extended by one more parameter at compile time
            var success = sendMoneyUseCase.sendMoney(command, continuation)

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
