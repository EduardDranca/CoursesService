package com.freecourses

import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = [BaseIntegrationTest.Companion.DynamoDBInitializer::class])
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var ddbTable: DynamoDbTable<CourseDO>

    @BeforeAll
    fun setUpAll() {
        this.ddbTable.createTable(
                CreateTableEnhancedRequest.builder()
                        .globalSecondaryIndices(
                                EnhancedGlobalSecondaryIndex.builder()
                                        .indexName(CourseDO.INDEX_NAME)
                                        .projection(
                                                Projection.builder()
                                                        .projectionType(ProjectionType.ALL)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        )
    }


    companion object {
        @Container
        private val ddbContainer = GenericContainer("amazon/dynamodb-local:latest")
            .withExposedPorts(8000)

        init {
            ddbContainer.start()
        }

        class DynamoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
            override fun initialize(ctx: ConfigurableApplicationContext?) {
                TestPropertyValues.of( "dynamodb.endpoint: http://${ddbContainer.host}:${ddbContainer.firstMappedPort}")
                    .applyTo(ctx)
            }
        }
    }
}