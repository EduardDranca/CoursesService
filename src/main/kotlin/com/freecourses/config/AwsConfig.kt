package com.freecourses.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

@Configuration
open class AwsConfig {

    @Bean
    @Profile("!test")
    open fun awsCredentialsProvider(): AwsCredentialsProvider {
        return DefaultCredentialsProvider.create()
    }

    @Bean
    @Profile("test")
    open fun awsCredentialsProviderTest(): AwsCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
    }
}