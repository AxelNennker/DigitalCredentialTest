package de.telekom.digitalcredentials.dc_lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data (

    @SerialName("response_type" ) var responseType : String?    = null,
    @SerialName("response_mode" ) var responseMode : String?    = null,
    @SerialName("nonce"         ) var nonce        : String?    = null,
    @SerialName("dcql_query"    ) var dcqlQuery    : DcqlQuery? = DcqlQuery()

)