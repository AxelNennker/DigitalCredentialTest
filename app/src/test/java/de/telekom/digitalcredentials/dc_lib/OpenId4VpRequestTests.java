package de.telekom.digitalcredentials.dc_lib;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class OpenId4VpRequestTests {

    @Test
    public void openId4VpRequestTest() throws JSONException {
        JSONObject dcql_credentials_object = new JSONObject(DcqlTests.DCQL_CREDENTIAL_OBJECT);

        OpenId4VpRequest openId4VpRequest = new OpenId4VpRequest(dcql_credentials_object);

        //assertEquals("", openId4VpRequest.toString());

   }
}
