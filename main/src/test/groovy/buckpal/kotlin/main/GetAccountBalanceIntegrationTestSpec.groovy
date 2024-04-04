package buckpal.kotlin.main

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@MicronautTest(transactional = false)
class GetAccountBalanceIntegrationTestSpec extends Specification {

    @Shared
    @AutoCleanup
    @Inject
    @Client("/accounts")
    HttpClient client

    def "Get Balance"() {
        when:
            HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/1/balance"), String)

        then:
            response.status == HttpStatus.OK
        and:
            response.body() == "500"
    }

}
