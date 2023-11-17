package com.freecourses.config

import com.freecourses.persistence.model.CourseDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import java.net.URI

@Configuration
open class DynamoDbConfig {
    @Autowired
    lateinit var environment: Environment

//    @Value("\${dynamodb.local.endpoint}")
    var dynamoDbLocalEndpoint: String = "https://localhost.localstack.cloud:4566";

    @Bean
    @Profile("!test")
    open fun amazonDynamoDB(credentialsProvider: AwsCredentialsProvider): DynamoDbClient {
        return DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    @Profile("test")
    open fun amazonDynamoDBTest(credentialsProvider: AwsCredentialsProvider): DynamoDbClient {
        return DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(dynamoDbLocalEndpoint))
            .build()
    }

    @Bean
    open fun amazonEnhancedDynamoDB(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }

    @Bean
    open fun dynamoDBCoursesTable(amazonEnhancedDynamoDB: DynamoDbEnhancedClient): DynamoDbTable<CourseDO> {
        val dynamoDBCoursesTable = amazonEnhancedDynamoDB.table("courses-table", TableSchema.fromBean(CourseDO::class.java))
        if (environment.matchesProfiles("test")) {
            createTestTable(dynamoDBCoursesTable)
        }
        return dynamoDBCoursesTable
    }

    private fun createTestTable(dynamoDBCoursesTable: DynamoDbTable<CourseDO>) {
        try {
            dynamoDBCoursesTable.createTable(
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
        } catch (ex: ResourceInUseException) {
            System.out.println("exception caught");
            // intentionally ignore this exception for local testing
        }
    }
}