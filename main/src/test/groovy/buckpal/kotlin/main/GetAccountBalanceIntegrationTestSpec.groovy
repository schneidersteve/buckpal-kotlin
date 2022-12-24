package buckpal.kotlin.main


import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static io.micronaut.http.HttpRequest.GET

@MicronautTest(transactional = false)
class GetAccountBalanceIntegrationTestSpec extends Specification {

    @Shared
    @AutoCleanup
    @Inject
    @Client("/accounts")
    HttpClient client

    def "Get Balance"() {
        when:
            String body = client.toBlocking().retrieve(GET("/1/balance"))

        then:
            body == "500"
    }

}
