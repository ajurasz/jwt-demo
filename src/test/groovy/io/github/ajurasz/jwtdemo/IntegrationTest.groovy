package io.github.ajurasz.jwtdemo

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

@Rollback
@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegrationTest extends Specification implements AsUser {
}
