package de.telekom.digitalcredentials.dc_lib

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import kotlinx.coroutines.coroutineScope
import org.junit.Test

class CredentialManagerTests {

    @Test
    fun test() {
        val requestJson = generateTs43DigitalCredentialRequestFromServer()
        val digiCredOption = GetDigitalCredentialOption(requestJson = requestJson)
        val getCredRequest = GetCredentialRequest(
            listOf(digiCredOption)
        )

        val credentialManager = CredentialManager.create(context)

        coroutineScope.launch {
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
            } catch (e : GetCredentialException) {
                // If user cancels the operation, the feature isn't available, or the
                // SIM doesn't support the feature, a GetCredentialCancellationException
                // will be returned. Otherwise, a GetCredentialUnsupportedException will
                // be returned with details in the exception message.
                handleFailure(e)
            }
        }
    }
}