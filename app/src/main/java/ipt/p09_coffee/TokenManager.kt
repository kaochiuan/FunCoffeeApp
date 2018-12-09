package ipt.p09_coffee

import android.content.Context
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject


object PreferenceHelper {
    private val ACCESS_TOKEN = "access_token"
    private val REFRESH_TOKEN = "refresh_token"

    fun saveAuthToken(context: Context, token: AuthToken) {
        val sp = context.getSharedPreferences("auth_profile", Context.MODE_PRIVATE)
        val profileEditor = sp!!.edit()
        profileEditor.putString("access_token", token.accessToken)
        profileEditor.putString("refresh_token", token.refreshToken)
        profileEditor.apply()
    }

    fun getAuthToken(context: Context): AuthToken {
        val sp = context.getSharedPreferences("auth_profile", Context.MODE_PRIVATE)

        return AuthToken(accessToken = sp!!.getString(ACCESS_TOKEN, null), refreshToken = sp.getString(REFRESH_TOKEN, null))
    }
}

class AuthToken(var accessToken: String, var refreshToken: String)


class TokenManager {
    companion object {
        fun refreshAccessToken(context: Context, refreshToken: String, serverDestination: String) {
            val temp = "Bearer $refreshToken"
            val client = AsyncHttpClient()
            client.addHeader("authorization", temp)
            client.addHeader("Content-Type", "application/json")
            val url = "$serverDestination/token/refresh"

            client.get(url, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    try {
                        val m_JUserInfo_Tmp = JSONObject(String(responseBody))

                        val accessToken = m_JUserInfo_Tmp.getString("access_token")

                        PreferenceHelper.saveAuthToken(context, AuthToken(accessToken, refreshToken))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    print(String(responseBody))
                }
            })
        }
    }
}