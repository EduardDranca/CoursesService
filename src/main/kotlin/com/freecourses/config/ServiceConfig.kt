package com.freecourses.config

import com.freecourses.persistence.CoursesRepository
import com.freecourses.persistence.model.CourseDO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class ServiceConfig {
    @Bean
    open fun coursesRepository(coursesTable: DynamoDbTable<CourseDO>, dynamoDbEnhancedClient: DynamoDbEnhancedClient): CoursesRepository {
        return CoursesRepository(coursesTable, dynamoDbEnhancedClient)
    }

    @Bean
    open fun awsCredentialsProvider(): AwsCredentialsProvider {
        return DefaultCredentialsProvider.create()
    }

    @Bean
    open fun amazonDynamoDB(credentialsProvider: AwsCredentialsProvider): DynamoDbClient {
        return DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
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
        return amazonEnhancedDynamoDB.table("Courses", TableSchema.fromBean(CourseDO::class.java))
    }

     //    private synchronized void createTable(AmazonDynamoDB db, DynamoDBMapper dbMapper) {
    //        CreateTableRequest t = dbMapper.generateCreateTableRequest(CourseDTO.class);
    //        t.setProvisionedThroughput(new ProvisionedThroughput(100L, 100L));
    //        t.getGlobalSecondaryIndexes().forEach(gsi -> {
    //            gsi.setProvisionedThroughput(new ProvisionedThroughput(100L, 100L));
    //            gsi.setProjection(new Projection()
    //                    .withProjectionType(ProjectionType.ALL));
    //        });
    //        db.createTable(t);
    //    }
}