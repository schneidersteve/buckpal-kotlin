package buckpal.kotlin.domain

import java.math.BigInteger

data class Money(val amount: BigInteger) {

    fun isPositiveOrZero(): Boolean {
        return amount.compareTo(BigInteger.ZERO) >= 0
    }

    fun isNegative(): Boolean {
        return amount.compareTo(BigInteger.ZERO) < 0
    }

    fun isPositive(): Boolean {
        return amount.compareTo(BigInteger.ZERO) > 0
    }

    fun isGreaterThanOrEqualTo(money: Money): Boolean {
        return amount.compareTo(money.amount) >= 0
    }

    fun isGreaterThan(money: Money): Boolean {
        return amount.compareTo(money.amount) >= 1
    }

    fun minus(money: Money): Money {
        return Money(amount.subtract(money.amount))
    }

    fun plus(money: Money): Money {
        return Money(amount.add(money.amount))
    }

    fun negate(): Money {
        return Money(amount.negate())
    }

    companion object {
        val ZERO: Money = Money.of(0L)

        fun of(value: Long): Money {
            return Money(BigInteger.valueOf(value))
        }

        fun add(a: Money, b: Money): Money {
            return Money(a.amount.add(b.amount))
        }

        fun subtract(a: Money, b: Money): Money {
            return Money(a.amount.subtract(b.amount))
        }
    }
}
