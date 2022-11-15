# Example Implementation of a Hexagonal Architecture

Inspired by https://github.com/thombergs/buckpal

## Tech Stack

* [Kotlin](https://kotlinlang.org)
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
* [Spock](https://github.com/spockframework/spock)
* [Micronaut](https://micronaut.io)
* [Micronaut Data - R2DBC](https://micronaut-projects.github.io/micronaut-data/latest/guide/#dbc)
* [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download)

## Layers and Dependency Inversion

![Dependency Inversion](di.png)

## Send Money Use Case

```gherkin
Feature: Send Money

  Scenario: Transaction succeeds
    Given a source account
    And a target account

    When money is send

    Then send money succeeds

    And source account is locked
    And source account withdrawal will succeed
    And source account is released

    And target account is locked
    And target account deposit will succeed
    And target account is released

    And accounts have been updated
```
