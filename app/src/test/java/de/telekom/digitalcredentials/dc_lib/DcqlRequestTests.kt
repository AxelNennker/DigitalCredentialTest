package de.telekom.digitalcredentials.dc_lib

var MOCK_DCQL_REQUEST: () -> Credentials = {
    val vctValues = arrayListOf("number-verification/device-phone-number/ts43")
    val credentialAuthorizationJwt = ""

    val dcqlRequest = Credentials()
    dcqlRequest.id = "aggregator1"
    dcqlRequest.format = "dc-authorization+sd-jwt"
    dcqlRequest.meta = Meta(vctValues, credentialAuthorizationJwt)
    val claimSubscriptionHing = Claim(arrayListOf("subscription_hint"),
        arrayListOf("1"))
    val claimPhonenumberHint = Claim(arrayListOf("phone_number_hint"),
        arrayListOf("+14155552671"))
    dcqlRequest.claims = arrayListOf(claimSubscriptionHing, claimPhonenumberHint)
    dcqlRequest
}

class DcqlRequestTests {

}