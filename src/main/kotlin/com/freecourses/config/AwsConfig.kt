package com.freecourses.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import java.net.URI
import java.util.function.Supplier

@Configuration
open class AwsConfig {
    @Value("\${dynamodb.role-arn}") lateinit var ddbRoleArn: String
    @Value("\${dynamodb.session-name:ddb-session}") lateinit var sessionName: String
    @Value("\${sts.endpoint}") lateinit var stsEndpiont: String

    @Bean("service-execution-role")
    open fun awsCredentialsProvider(): AwsCredentialsProvider {
        return DefaultCredentialsProvider.create()
    }

    @Bean
    @Profile("!local")
    open fun stsClient(@Qualifier("service-execution-role") serviceCredentials: AwsCredentialsProvider): StsClient {
        return StsClient.builder()
                .credentialsProvider(serviceCredentials)
                .build()
    }

    @Bean
    @Profile("local")
    open fun stsClientLocal(@Qualifier("service-execution-role") serviceCredentials: AwsCredentialsProvider): StsClient {
        return StsClient.builder()
                .credentialsProvider(serviceCredentials)
                .endpointOverride(URI(stsEndpiont))
                .build()
    }

    @Bean("ddb-access-role")
    open fun dynamoDBRole(stsClient: StsClient): AwsCredentialsProvider {
        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(Supplier {
                    AssumeRoleRequest.builder()
                        .roleArn(ddbRoleArn)
                        .roleSessionName(sessionName)
                        .build()
                })
                .build()
    }
}
