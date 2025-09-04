package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DcqlRequest (

  @SerialName("id"     ) var id     : String?           = null,
  @SerialName("format" ) var format : String?           = null,
  @SerialName("meta"   ) var meta   : Meta?             = Meta(),
  @SerialName("claims" ) var claims : ArrayList<Claim> = arrayListOf()

)