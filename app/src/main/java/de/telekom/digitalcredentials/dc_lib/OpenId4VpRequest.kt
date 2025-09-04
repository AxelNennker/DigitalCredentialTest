package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenId4VpRequest(
    @SerialName("requests" ) var requests : ArrayList<OpenId4Vp> = arrayListOf()
)
