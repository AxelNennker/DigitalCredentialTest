package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Claim (

  @SerialName("path"   ) var path   : ArrayList<String> = arrayListOf(),
  @SerialName("values" ) var values : ArrayList<String>    = arrayListOf()

)