package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AggregatorSession (

  @SerialName("enc_key"     ) var encKey     : String? = null,
  @SerialName("nonce"       ) var nonce      : String? = null,
  @SerialName("session_key" ) var sessionKey : String? = null

)