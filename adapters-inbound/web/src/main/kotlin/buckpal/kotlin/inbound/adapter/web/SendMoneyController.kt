package buckpal.kotlin.inbound.adapter.web

import buckpal.kotlin.application.SendMoneyCommand
import buckpal.kotlin.application.SendMoneyUseCase
import buckpal.kotlin.common.WebAdapter
import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.delay

@WebAdapter
@Controller("/accounts")
open class SendMoneyController(private val sendMoneyUseCase: SendMoneyUseCase) {

    @Post("/send/{sourceAccountId}/{targetAccountId}/{amount}")
    suspend fun sendMoney(
        @PathVariable("sourceAccountId") sourceAccountId: Long,
        @PathVariable("targetAccountId") targetAccountId: Long,
        @PathVariable("amount") amount: Long,
    ) {
        val command = SendMoneyCommand(
            AccountId(sourceAccountId),
            AccountId(targetAccountId),
            Money.of(amount)
        )

        sendMoneyUseCase.sendMoney(command)

//        delay(1000)
    }

}
