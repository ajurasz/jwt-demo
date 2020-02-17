package io.github.ajurasz.jwtdemo

import io.github.ajurasz.jwtdemo.jwt.TokenGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.RequestPostProcessor

trait AsUser {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private TokenGenerator tokenGenerator

    def USER = [username: "admin"]

    ResultActions asAnonymousUser(Closure<MockHttpServletRequestBuilder> builder) {
        mockMvc.perform(builder.call().contentType(MediaType.APPLICATION_JSON))
    }

    ResultActions asAuthenticatedUser(Map<String, Object> properties = [:],
                                      Closure<MockHttpServletRequestBuilder> builder) {
        mockMvc.perform(builder.call().contentType(MediaType.APPLICATION_JSON).with(addHeaders(properties)))
    }

    private RequestPostProcessor addHeaders(Map<String, Object> properties) {
        def user = USER + properties
        { MockHttpServletRequest request ->
            request.addHeader("Authorization", "Bearer ${generateToken(user.username as String)}")
            request
        }
    }

    private String generateToken(String email) {
        tokenGenerator.generateFor(email)
    }
}
