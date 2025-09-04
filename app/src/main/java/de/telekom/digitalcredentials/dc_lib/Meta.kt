package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta (

  @SerialName("vct_values"                   ) var vctValues                  : ArrayList<String> = arrayListOf(),
  @SerialName("credential_authorization_jwt" ) var credentialAuthorizationJwt : String?           = null

)