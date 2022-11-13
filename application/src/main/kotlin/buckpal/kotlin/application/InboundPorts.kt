package buckpal.kotlin.application

import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money

interface SendMoneyUseCase {
    suspend fun sendMoney(command: SendMoneyCommand): Boolean
}

// TODO implement reflection free validating
data class SendMoneyCommand(val sourceAccountId: AccountId, val targetAccountId: AccountId, val money: Money) {}
