package buckpal.kotlin.application

import buckpal.kotlin.domain.AccountId
import buckpal.kotlin.domain.Money

interface SendMoneyUseCase {
    fun sendMoney(command: SendMoneyCommand): Boolean
}

// TODO implement reflection free validating
class SendMoneyCommand(val sourceAccountId: AccountId, val targetAccountId: AccountId, val money: Money) {}
