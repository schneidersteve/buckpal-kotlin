package buckpal.kotlin.inbound.adapter.web

import buckpal.kotlin.application.GetAccountBalanceQuery
import buckpal.kotlin.domain.ar.AccountId
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*

@Controller("/accounts")
open class GetAccountBalanceController(private val getAccountBalanceQuery: GetAccountBalanceQuery) {

    @Get("/{accountId}/balance")
    @Produces(
        MediaType.TEXT_PLAIN
    )
    suspend fun getAccountBalance(
        @PathVariable("accountId") accountId: Long,
    ): Long {
        return getAccountBalanceQuery.getAccountBalance(AccountId(accountId)).amount.toLong()
    }

}
