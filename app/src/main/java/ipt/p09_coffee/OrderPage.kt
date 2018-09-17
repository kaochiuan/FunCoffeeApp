package ipt.p09_coffee

import android.app.Activity
import android.os.Bundle

class OrderPage : Activity() {
    private var serverDestination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        setContentView(R.layout.orderpage)
    }
}
