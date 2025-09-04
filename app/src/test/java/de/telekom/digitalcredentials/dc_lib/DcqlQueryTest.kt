package de.telekom.digitalcredentials.dc_lib

import org.junit.Test

class DcqlQueryTest {


    @Test
    fun test() {
        val dcqlQuery = DcqlQuery()
        dcqlQuery.credentials = arrayListOf(MOCK_DCQL_REQUEST())
    }
}