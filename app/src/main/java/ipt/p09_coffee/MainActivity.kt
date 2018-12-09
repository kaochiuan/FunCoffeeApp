package ipt.p09_coffee

import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header


class MainActivity : Activity() {
    val DEFAULT_TIMEOUT = 20 * 1000
    var fg_Login: Boolean? = null
    private var btLogin: Button? = null
    private var btRegister: Button? = null
    private var etUserName: EditText? = null
    private var etUserPw: EditText? = null
    private var etUserEmail: EditText? = null
    private var serverDestination: String? = null

    var btLoginListener: View.OnClickListener = View.OnClickListener {
        val params = RequestParams()
        val m_UserName = etUserName!!.text.toString()
        val m_UserPw = etUserPw!!.text.toString()

        val fg_Pass = false
        if (fg_Pass) {

            val intent = Intent()
            intent.setClass(this@MainActivity, MainPage::class.java)

            startActivity(intent)
            Toast.makeText(applicationContext, "登入成功", Toast.LENGTH_LONG).show()
        } else {

            if (m_UserName.isNotBlank() && m_UserName.isNotEmpty() &&
                    m_UserPw.isNotBlank() && m_UserPw.isNotEmpty()) {
                params.put("username", m_UserName)
                params.put("password", m_UserPw)

                invoke_Login("$serverDestination/login", params)

                Toast.makeText(applicationContext, "登入中", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Username and password cannot be empty or blank", Toast.LENGTH_LONG).show()
            }
        }
    }


    var btRegisterListener: View.OnClickListener = View.OnClickListener {
        val params = RequestParams()
        val m_UserName = etUserName!!.text.toString()
        val m_UserPw = etUserPw!!.text.toString()
        val m_UserEmail = etUserEmail!!.text.toString()

        if (m_UserName.isNotEmpty() && m_UserName.isNotBlank() &&
                m_UserPw.isNotEmpty() && m_UserPw.isNotBlank() &&
                m_UserEmail.isNotEmpty() && m_UserEmail.isNotBlank()) {
            params.put("username", m_UserName)
            params.put("password", m_UserPw)
            params.put("email", m_UserEmail)

            invoke_Register("$serverDestination/user/registration", params)

        } else {
            Toast.makeText(applicationContext, "Username, password and email cannot be empty or blank", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        setContentView(R.layout.main)

        buildViews()
        fg_Login = false
    }

    private fun buildViews() {

        btLogin = findViewById(R.id.Btid_Login) as Button
        btRegister = findViewById(R.id.Btid_Register) as Button
        etUserName = findViewById(R.id.Edid_UserName) as EditText
        etUserPw = findViewById(R.id.Edid_UserPw) as EditText
        etUserEmail = findViewById(R.id.Edid_UserEmail) as EditText

        btLogin!!.setOnClickListener(btLoginListener)
        btRegister!!.setOnClickListener(btRegisterListener)
    }

    fun invoke_Login(context: String, params: RequestParams) {
        try {
            val client = AsyncHttpClient()
            client.setTimeout(DEFAULT_TIMEOUT)
            client.post(context, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    val response = String(responseBody)
                    var message = ""
                    fg_Login = true

                    try {
                        val jsonRootObject = JSONObject(response)
                        val accessToken = jsonRootObject.getString("access_token")
                        val refreshToken = jsonRootObject.getString("refresh_token")
                        message = jsonRootObject.getString("message")

                        PreferenceHelper.saveAuthToken(this@MainActivity,
                                AuthToken(accessToken, refreshToken))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }


                    // goto main page
                    val intent = Intent()
                    intent.setClass(this@MainActivity, MainPage::class.java)

                    // start next page
                    startActivity(intent)
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    val response = String(responseBody)
                    val jsonRootObject = JSONObject(response)
                    Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: Exception) {
            android.widget.Toast.makeText(applicationContext, "請檢查您的網路是否正常連線", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun invoke_Register(context: String, params: RequestParams) {
        val client = AsyncHttpClient()
        client.setTimeout(DEFAULT_TIMEOUT)
        client.post(context, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val response = String(responseBody)
                val jsonRootObject = JSONObject(response)
                Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val response = String(responseBody)
                val jsonRootObject = JSONObject(response)

                if (statusCode == 404) {
                    Toast.makeText(applicationContext, "Requested resource not found", Toast.LENGTH_LONG).show()
                } else if (statusCode == 500) {
                    Toast.makeText(applicationContext, "Something went wrong at server end", Toast.LENGTH_LONG).show()
                } else if (statusCode == 400) {
                    Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "帳號註冊失敗", Toast.LENGTH_LONG).show()
                }
            }
        })

    }


}

