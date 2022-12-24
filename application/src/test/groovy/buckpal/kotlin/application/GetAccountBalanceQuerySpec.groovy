package buckpal.kotlin.application

import buckpal.kotlin.application.q.GetAccountBalanceQueryImpl
import buckpal.kotlin.domain.ar.Account
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.Money
import kotlin.coroutines.Continuation
import kotlinx.coroutines.Dispatchers
import spock.lang.Specification

class GetAccountBalanceQuerySpec extends Specification {

    LoadAccountPort loadAccountPort = Mock()

    GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQueryImpl(loadAccountPort)

    // Kotlin suspend function parameter
    var continuation = Mock(Continuation) {
        getContext() >> Dispatchers.Default
    }

    def "Succeeds"() {
        given: "a account"
            Account account = Mock()
            var accountId = new AccountId(41L)
            loadAccountPort.loadAccount(accountId, _, _) >> account

        when: "balance is queried"
            var balance = getAccountBalanceQuery.getAccountBalance(accountId, continuation)

        then: "balance is 500"
            balance == Money.@Companion.of(500L)
        and: "account balance is 500"
            1 * account.calculateBalance() >> Money.@Companion.of(500L)
    }
}
