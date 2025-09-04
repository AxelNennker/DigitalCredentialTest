package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenId4Vp (

    @SerialName("protocol" ) var protocol : String? = null,
    @SerialName("data"     ) var data     : Data?   = Data()

)