package com.messaging.platform.naver.config

object NaverApi {
    const val SEND_PATH_TEMPLATE = "/naver/v1/services/%s/messages"

    const val HEADER_TIMESTAMP = "x-ncp-apigw-timestamp"
    const val HEADER_ACCESS_KEY = "x-ncp-iam-access-key"
    const val HEADER_SIGNATURE = "x-ncp-apigw-signature-v2"

    const val SIGNATURE_ALGORITHM = "HmacSHA256"
}
