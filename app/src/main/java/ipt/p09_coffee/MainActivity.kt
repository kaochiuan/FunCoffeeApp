package ipt.p09_coffee

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header


class MainActivity : Activity() {

    var errorMsg: TextView? = null
    var m_accessToken: String = ""
    var m_refreshToken: String = ""
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
        val m_UserEmail = etUserEmail!!.text.toString()

        val fg_Pass = false
        if (fg_Pass) {
            // 進入主畫面
            val intent = Intent()
            intent.setClass(this@MainActivity, MainPage::class.java)

            // 夾帶資料進入下一個頁面
            val bundle = Bundle()
            bundle.putString("accessToken", "")
            bundle.putString("refreshToken", "")
            intent.putExtras(bundle)

            // 啟動下一個頁面
            startActivity(intent)
            Toast.makeText(applicationContext, "登入成功", Toast.LENGTH_LONG).show()
        } else {

            if(m_UserName.isNotBlank() && m_UserName.isNotEmpty() &&
                    m_UserPw.isNotBlank() && m_UserPw.isNotEmpty()){
                // Put Http parameter username with value of Email Edit View control
                params.put("username", m_UserName)
                // Put Http parameter password with value of Password Edit Value control
                params.put("password", m_UserPw)

                invoke_Login("$serverDestination/login", params)

                Toast.makeText(applicationContext, "登入中", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext, "Username and password cannot be empty or blank", Toast.LENGTH_LONG).show()
            }
        }
    }


    var btRegisterListener: View.OnClickListener = View.OnClickListener {
        val params = RequestParams()
        val m_UserName = etUserName!!.text.toString()
        val m_UserPw = etUserPw!!.text.toString()
        val m_UserEmail = etUserEmail!!.text.toString()

        if(m_UserName.isNotEmpty() && m_UserName.isNotBlank() &&
                m_UserPw.isNotEmpty() && m_UserPw.isNotBlank() &&
                m_UserEmail.isNotEmpty() && m_UserEmail.isNotBlank()) {
            // Put Http parameter username with value of Email Edit View control
            params.put("username", m_UserName)
            // Put Http parameter password with value of Password Edit Value control
            params.put("password", m_UserPw)
            params.put("email", m_UserEmail)

            invoke_Register("$serverDestination/user/registration", params)

            //Toast.makeText(getApplicationContext(), "Register Button", Toast.LENGTH_LONG).show();
        }else{
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
        val client = AsyncHttpClient()
        client.post(context, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val response = String(responseBody)
                var message =""
                fg_Login = true

                try {
                    val jsonRootObject = JSONObject(response)
                    m_accessToken = jsonRootObject.getString("access_token")
                    m_refreshToken = jsonRootObject.getString("refresh_token")
                    message = jsonRootObject.getString("message")
                    //Toast.makeText(getApplicationContext(), m_accessToken, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), m_refreshToken, Toast.LENGTH_LONG).show();
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                // 進入主畫面
                val intent = Intent()
                intent.setClass(this@MainActivity, MainPage::class.java)

                // 夾帶資料進入下一個頁面
                val bundle = Bundle()
                bundle.putString("accessToken", m_accessToken)
                bundle.putString("refreshToken", m_refreshToken)
                intent.putExtras(bundle)

                // 啟動下一個頁面
                startActivity(intent)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val response = String(responseBody)
                val jsonRootObject = JSONObject(response)
                Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun invoke_Register(context: String, params: RequestParams) {
        val client = AsyncHttpClient()

        client.post(context, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val response = String(responseBody)
                val jsonRootObject = JSONObject(response)
                Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val response = String(responseBody)
                val jsonRootObject = JSONObject(response)
                // Hide Progress Dialog
                //prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(applicationContext, "Requested resource not found", Toast.LENGTH_LONG).show()
                } else if (statusCode == 500) {
                    Toast.makeText(applicationContext, "Something went wrong at server end", Toast.LENGTH_LONG).show()
                } else if (statusCode == 400) {
                    Toast.makeText(applicationContext, jsonRootObject.getString("message"), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "帳號註冊失敗", Toast.LENGTH_LONG).show()
                }
            }
        })

    }


}

