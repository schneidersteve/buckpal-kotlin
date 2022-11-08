package buckpal.kotlin.application

import buckpal.kotlin.domain.AccountId
import jakarta.inject.Singleton

@Singleton
class NoOpAccountLock : AccountLock {
    override fun lockAccount(accountId: AccountId) {
        // do nothing
    }

    override fun releaseAccount(accountId: AccountId) {
        // do nothing
    }
}
