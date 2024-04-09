package buckpal.kotlin.inbound.adapter.web

import buckpal.kotlin.application.SendMoneyCommand
import buckpal.kotlin.application.SendMoneyUseCase
import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.Money
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

@Controller("/accounts")
internal open class SendMoneyController(private val sendMoneyUseCase: SendMoneyUseCase) {

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
    }

}
