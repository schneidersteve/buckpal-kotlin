package buckpal.kotlin.application

import buckpal.kotlin.domain.ar.AccountId
import buckpal.kotlin.domain.vo.Money

interface SendMoneyUseCase {
    suspend fun sendMoney(command: SendMoneyCommand): Boolean
}

// TODO implement reflection free validating
data class SendMoneyCommand(val sourceAccountId: AccountId, val targetAccountId: AccountId, val money: Money) {}
