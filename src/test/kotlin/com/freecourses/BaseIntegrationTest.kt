package com.freecourses

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = [BaseIntegrationTest.Companion.DynamoDBInitializer::class])
@Testcontainers
open class BaseIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        @Container
        private val ddbContainer = GenericContainer("amazon/dynamodb-local:latest")
            .withExposedPorts(8000)

        class DynamoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
            override fun initialize(ctx: ConfigurableApplicationContext?) {
                TestPropertyValues.of( "dynamodb.local.endpoint: http://${ddbContainer.host}:${ddbContainer.firstMappedPort}")
                    .applyTo(ctx)
            }
        }
    }
}