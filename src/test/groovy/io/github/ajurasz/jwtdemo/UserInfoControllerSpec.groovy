package io.github.ajurasz.jwtdemo

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserInfoControllerSpec extends IntegrationTest {
    def "should receive 401 for unauthenticated request"() {
        expect:
        asAnonymousUser { get('/api/user-info') }
                .andExpect(status().isUnauthorized())
    }

    def "should retrieve user-info"() {
        expect:
        asAuthenticatedUser { get('/api/user-info') }
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.username', is('admin')))
    }

    def "should retrieve user-info using custom user"() {
        expect:
        asAuthenticatedUser([username: 'user']) { get('/api/user-info') }
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.username', is('user')))
    }
}
