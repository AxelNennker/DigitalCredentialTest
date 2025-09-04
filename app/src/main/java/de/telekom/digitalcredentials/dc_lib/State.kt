package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class State (

  @SerialName("aggregator_session" ) var aggregatorSession : AggregatorSession? = AggregatorSession(),
  @SerialName("credential_type"    ) var credentialType    : String?            = null,
  @SerialName("nonce"              ) var nonce             : String?            = null,
  @SerialName("private_key"        ) var privateKey        : String?            = null,
  @SerialName("public_key"         ) var publicKey         : String?            = null

)