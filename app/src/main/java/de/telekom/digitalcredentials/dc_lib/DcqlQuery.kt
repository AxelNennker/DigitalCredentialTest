package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DcqlQuery (

  @SerialName("credentials" ) var credentials : ArrayList<Credentials> = arrayListOf()

)