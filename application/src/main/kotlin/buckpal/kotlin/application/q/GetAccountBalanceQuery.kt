package buckpal.kotlin.application.q

import buckpal.kotlin.application.GetAccountBalanceQuery
import buckpal.kotlin.application.LoadAccountPort
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.Money
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class GetAccountBalanceQueryImpl(
    private val loadAccountPort: LoadAccountPort,
) : GetAccountBalanceQuery {
    override suspend fun getAccountBalance(accountId: AccountId): Money {
        return loadAccountPort.loadAccount(accountId, LocalDateTime.now()).calculateBalance()
    }
}
