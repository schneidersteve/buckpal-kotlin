package buckpal.kotlin.main

import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.domain.Account
import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import kotlin.coroutines.Continuation
import kotlinx.coroutines.Dispatchers
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

import static io.micronaut.http.HttpRequest.POST

@MicronautTest(transactional = false)
class SendMoneyIntegrationTestSpec extends Specification {

    @Shared
    @AutoCleanup
    @Inject
    @Client("/accounts")
    HttpClient client

    @Shared
    @Inject
    LoadAccountPort loadAccountPort

    // Kotlin suspend function parameter
    var continuation = Mock(Continuation) {
        getContext() >> Dispatchers.Default
    }

    def "Send Money"() {
        given: "initial source account balance"
            var sourceAccountId = new AccountId(1L)
            Account sourceAccount = loadAccountPort.loadAccount(sourceAccountId, LocalDateTime.now(), continuation)
            var initialSourceBalance = sourceAccount.calculateBalance()

        and: "initial target account balance"
            var targetAccountId = new AccountId(2L)
            Account targetAccount = loadAccountPort.loadAccount(targetAccountId, LocalDateTime.now(), continuation)
            var initialTargetBalance = targetAccount.calculateBalance()
        and:
            var money = Money.@Companion.of(500L)

        when: "money is send"
            HttpResponse response = client.toBlocking().exchange(POST("""/send/$sourceAccountId.value/$targetAccountId.value/$money.amount""", ""))

            sourceAccount = loadAccountPort.loadAccount(sourceAccountId, LocalDateTime.now(), continuation)
            targetAccount = loadAccountPort.loadAccount(targetAccountId, LocalDateTime.now(), continuation)

        then: "http status is OK"
            response.status == HttpStatus.OK

        and: "source account balance is correct"
            sourceAccount.calculateBalance() == initialSourceBalance.minus(money)

        and: "target account balance is correct"
            targetAccount.calculateBalance() == initialTargetBalance.plus(money)
    }

}
