package de.telekom.digitalcredentials

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jose.util.Base64
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import de.telekom.digitalcredentials.databinding.ActivityMainBinding
import de.telekom.digitalcredentials.dc_lib.Credentials
import de.telekom.digitalcredentials.dc_lib.Data
import de.telekom.digitalcredentials.dc_lib.DcqlQuery
import de.telekom.digitalcredentials.dc_lib.Meta
import de.telekom.digitalcredentials.dc_lib.OpenId4Vp
import de.telekom.digitalcredentials.dc_lib.OpenId4VpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.RandomStringUtils.secure
import java.time.Instant
import java.util.Date

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    val key: ECKey = ECKeyGenerator(Curve.P_256)
        .keyID("123")
        .generate()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.fab.setOnClickListener { view ->
            GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
                getPhoneNumber(view.context)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

@OptIn(ExperimentalDigitalCredentialApi::class)
private suspend fun MainActivity.getPhoneNumber(activityContext: Context) {
    val credentialManager = CredentialManager.create(activityContext)

    val requestJson = getTestRequestJson()
    val digiCredOption = GetDigitalCredentialOption(requestJson = requestJson)
    val getCredRequest = GetCredentialRequest(
        listOf(digiCredOption)
    )

    try {
        val response = credentialManager.getCredential(
            context = activityContext,
            request = getCredRequest
        )
        val credential = response.credential
        when (credential) {
            is DigitalCredential -> {
                val responseJson = credential.credentialJson
                validateResponseOnServer(responseJson)
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential ${credential.type}")
            }
        }
    } catch (e: GetCredentialException) {
        // If user cancels the operation, the feature isn't available, or the
        // SIM doesn't support the feature, a GetCredentialCancellationException
        // will be returned. Otherwise, a GetCredentialUnsupportedException will
        // be returned with details in the exception message.
        handleFailure(e)
    }
}

private fun MainActivity.getTestRequestJson(): String {
    val protocol = "openid4vp-v1-unsigned"
    val responseType = "vp_token"
    val responseMode = "dc_api"
    val nonce = secure().nextAlphanumeric(32);
    val credentialAuthorizationJwt = getTestCredentialAuthorizationJwt()
    val credentials = Credentials(
        id = "aggregator1",
        format = "dc-authorization+sd-jwt",
        meta = Meta(
            arrayListOf("number-verification/device-phone-number/ts43"),
            credentialAuthorizationJwt
        )
    )
    val dcqlQuery = DcqlQuery(arrayListOf(credentials))
    val data = Data(responseType, responseMode, nonce, dcqlQuery)
    val openId4Vp = OpenId4Vp(protocol, data)

    val requests = OpenId4VpRequest(arrayListOf(openId4Vp))

    Log.d(TAG, String.format("requestJson: %s", Json.encodeToString(requests)))
    return Json.encodeToString(requests)

}

private fun MainActivity.getTestCredentialAuthorizationJwt(): String {
     val cert =
        Base64("MIICpTCCAkugAwIBAgIUC9fNJpdUMQYdBl1nh8+RitRwMD8wCgYIKoZIzj0EAwIweDELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDU1vdW50YWluIFZpZXcxGzAZBgNVBAoMEkV4YW1wbGUgQWdncmVnYXRvcjEfMB0GA1UEAwwWZXhhbXBsZS1hZ2dyZWdhdG9yLmRldjAeFw0yNTA1MTEyMjQwMDVaFw0zNTA0MjkyMjQwMDVaMHgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1Nb3VudGFpbiBWaWV3MRswGQYDVQQKDBJFeGFtcGxlIEFnZ3JlZ2F0b3IxHzAdBgNVBAMMFmV4YW1wbGUtYWdncmVnYXRvci5kZXYwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAARQqnKLl9Sh8tW03HyiPg9TTpirAX6WhZ+9IIhUXRFp9qDS4ynXxmFn33ZNg19PGUsEjq4l3joOzxvpxjX4h/Reo4GyMIGvMB0GA1UdDgQWBBQAWR9s4kXTcxrOy1KHMvRWSJH9bjAfBgNVHSMEGDAWgBQAWR9s4kXTcxrOy1KHMvRWSJH9bjAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIHgDApBgNVHRIEIjAghh5odHRwczovL2V4YW1wbGUtYWdncmVnYXRvci5jb20wIQYDVR0RBBowGIIWZXhhbXBsZS1hZ2dyZWdhdG9yLmNvbTAKBggqhkjOPQQDAgNIADBFAiBxDQ9Fbo/DQTdmSZKCTEIG9vfkBdYNcTw1RI3OI6/nJQIhAL56e7bEM99RM1SP02wx3lxqdVBZxbTHIrYBBF7cAsb3")
    val x5c = List(1) { cert }
    val header = JWSHeader.Builder(JWSAlgorithm.ES256)
        .type(JOSEObjectType("oauth-authz-req+jwt"))
        .keyID(key.keyID)
        .x509CertChain(x5c)
        .build();
    val encryptedResponseEncValuesSupported = List(1) { "A128GCM" }
    val jwks = List(1) { key.toECPublicKey()}
    val payload = JWTClaimsSet.Builder()
        .issuer("dcaggregator.dev")
        // FIXME .audience("you")
        // FIXME .subject("bob")
        .claim("encrypted_response_enc_values_supported", encryptedResponseEncValuesSupported)
        .claim("jwks", jwks)
        .expirationTime(Date.from(Instant.now().plusSeconds(120)))
        .build()

    val signedJWT = SignedJWT(header, payload)
    signedJWT.sign(ECDSASigner(key.toECPrivateKey()))
    val jwt: String = signedJWT.serialize()
    return jwt
}

private fun MainActivity.validateResponseOnServer(responseJson: String): Int {
    Log.i(TAG, responseJson)
    return 0
}

private fun MainActivity.handleFailure(e: GetCredentialException) {
    Log.e(TAG, e.toString())
}

