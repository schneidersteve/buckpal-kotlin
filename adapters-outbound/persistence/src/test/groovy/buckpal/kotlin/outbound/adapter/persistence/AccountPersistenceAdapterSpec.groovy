package buckpal.kotlin.outbound.adapter.persistence

import buckpal.kotlin.domain.ar.Account
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.ActivityWindow
import buckpal.kotlin.domain.vo.Money
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import kotlin.coroutines.Continuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowKt
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

import static buckpal.kotlin.testdata.AccountTestDataKt.defaultAccount
import static buckpal.kotlin.testdata.ActivityTestDataKt.defaultActivity

@MicronautTest(transactional = false)
class AccountPersistenceAdapterSpec extends Specification {

    AccountRepository accountRepository = Mock()

    ActivityRepository activityRepository = Mock()

    AccountPersistenceAdapter adapterUnderTest

    @Shared
    @Inject
    AccountMapper accountMapper

    // Kotlin suspend function parameter
    def continuation = Mock(Continuation) {
        getContext() >> Dispatchers.Default
    }

    def setup() {
        adapterUnderTest = new AccountPersistenceAdapter(accountRepository, activityRepository, accountMapper)
    }

    def "loads Account"() {
        given:
            var accountId = new AccountId(1L)
            var baselineDate = LocalDateTime.of(2018, 8, 10, 0, 0)
            // Kotlin suspend function is extended by one more parameter at compile time
            accountRepository.findById(accountId.value, _) >> new AccountEntity(1L)
            activityRepository.findByOwnerAccountIdEqualsAndTimestampGreaterThanEquals(accountId.value, baselineDate) >> FlowKt.asFlow([
                new ActivityEntity(
                    5,
                    LocalDateTime.of(2019, 8, 9, 9, 0),
                    1,
                    1,
                    2,
                    1000
                ),
                new ActivityEntity(
                    7,
                    LocalDateTime.of(2019, 8, 9, 10, 0),
                    1,
                    2,
                    1,
                    1000
                )
            ])
            // Kotlin suspend function is extended by one more parameter at compile time
            activityRepository.getWithdrawalBalanceUntil(accountId.value, baselineDate, _) >> 500L
            // Kotlin suspend function is extended by one more parameter at compile time
            activityRepository.getDepositBalanceUntil(accountId.value, baselineDate, _) >> 1000L
        when:
            // Kotlin suspend function is extended by one more parameter at compile time
            Account account = adapterUnderTest.loadAccount(
                accountId,
                baselineDate,
                continuation
            )
        then:
            account.getActivityWindow().getActivities().size() == 2
        and:
            account.calculateBalance() == Money.@Companion.of(500L)
    }

    def "updates Activities"() {
        given:
            Account account = defaultAccount()
                .withBaselineBalance(Money.@Companion.of(555L))
                .withActivityWindow(new ActivityWindow(
                    defaultActivity()
                        .withId(null)
                        .withMoney(Money.@Companion.of(1L))
                        .build()))
                .build()
        when:
            adapterUnderTest.updateActivities(account, continuation)
        then:
            1 * activityRepository.save(_, _)
    }

}
