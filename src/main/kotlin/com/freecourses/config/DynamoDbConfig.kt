package com.freecourses.config

import com.freecourses.persistence.model.CourseDO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
open class DynamoDbConfig {
    @Value("\${dynamodb.endpoint}")
    lateinit var dynamoDbLocalEndpoint: String

    @Bean
    @Profile("!local & !test")
    open fun amazonDynamoDB(@Qualifier("ddb-access-role") credentialsProvider: AwsCredentialsProvider): DynamoDbClient {
        return DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    @Profile("local", "test")
    open fun amazonDynamoDbLocal(@Qualifier("ddb-access-role") credentialsProvider: AwsCredentialsProvider): DynamoDbClient {
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
        return amazonEnhancedDynamoDB.table("courses-table", TableSchema.fromBean(CourseDO::class.java))
    }
}