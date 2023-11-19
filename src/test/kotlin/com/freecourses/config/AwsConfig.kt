package com.freecourses.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

@TestConfiguration
open class AwsConfig {

    @Bean
    open fun testAwsCredentialsProvider(): AwsCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKeyId", "accessKey"))
    }
}