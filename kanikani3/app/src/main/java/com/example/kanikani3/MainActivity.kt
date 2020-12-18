package com.example.kanikani3

//import android.R

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.util.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    // 定数
    private val REQUEST_ENABLEBLUETOOTH = 1 // Bluetooth機能の有効化要求時の識別コード
    private val REQUEST_CONNECTDEVICE = 2 // デバイス接続要求時の識別コード


    // メンバー変数
    private var mBluetoothAdapter // BluetoothAdapter : Bluetooth処理で必要
            : BluetoothAdapter? = null
    private var mDeviceAddress = "" // デバイスアドレス
        var pref: SharedPreferences? = null
        val TAG = ""
        var b : String = "username"
        var c : String = "twittername"
        var d : String = "comment"
        var inputdata = arrayOfNulls<String>(size =3)

        @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //oncreateに書く
        // Access a Cloud Firestore instance from your Activity val db = FirebaseFirestore.getInstance()

        var pref: SharedPreferences? = null

        //oncreateに書く
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        if (pref != null) b = pref.getString("userdata", "").toString();
        if (pref != null) c = pref.getString("twitterdata", "").toString()
        if (pref != null) d = pref.getString("commnentdata", "").toString()

    TransButton.setOnClickListener {
        val intent = Intent(application, NextActivity2::class.java) //nextにわたす
        intent.putExtra("data", b)
        intent.putExtra("data2", c)
        intent.putExtra("data3", d)
        startActivity(intent)
    }

        //データ送信
        val suretigai = db.collection("suretigai")

        val userData = hashMapOf( //自分のデータを送信
                "username" to b,
                "twitter_id" to c,
                "comment" to d
        )
        suretigai.document(macAddr).set(userData)
        //データ送信ここまで

        // Bluetoothアダプタの取得
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish() // アプリ終了宣言
            return
        }


    val editText = findViewById<EditText>(R.id.edit_text)
    val button = findViewById<Button>(R.id.button)
    button.setOnClickListener{
        Toast.makeText(this, editText.text.toString(), Toast.LENGTH_SHORT).show()
    }
}

// 初回表示時、および、ポーズからの復帰時
    override fun onResume() {
        super.onResume()
        // Android端末のBluetooth機能の有効化要求
        requestBluetoothFeature()

        if(pref!=null) {
            val e = pref!!.edit()
            e.putString("Stingdata", b)
            e.putString("twitterdata", c)
            e.putString("comentdata" ,d)
            e.commit()
        }
    }


    private fun requestBluetoothFeature() {
        if (mBluetoothAdapter?.isEnabled!!) {
            return
        }
    }

// 機能の有効化ダイアログの操作結果
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
        REQUEST_ENABLEBLUETOOTH -> if (RESULT_CANCELED == resultCode) {    // 有効にされなかった
            Toast.makeText(this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT).show()
            finish() // アプリ終了宣言
            return
        }
        REQUEST_CONNECTDEVICE -> {
//            val strDeviceName: String?
            var strDeviceName = ""

            if (RESULT_OK == resultCode) {
                // デバイスリストアクティビティからの情報の取得
                if (data != null) {
                    strDeviceName = data.getStringExtra(DeviceListActivity.Foo.EXTRAS_DEVICE_NAME)!!
                }
                if (data != null) {
                    mDeviceAddress = data.getStringExtra(DeviceListActivity.Foo.EXTRAS_DEVICE_ADDRESS)!!
                }
            } else {
                strDeviceName = ""
                mDeviceAddress = ""
            }
            (findViewById<View>(R.id.textview_devicename) as TextView).text = strDeviceName
            (findViewById<View>(R.id.textview_deviceaddress) as TextView).text = mDeviceAddress
        }
    }
    super.onActivityResult(requestCode, resultCode, data)
}

//    // 機能の有効化ダイアログの操作結果
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            REQUEST_ENABLEBLUETOOTH -> if (Activity.RESULT_CANCELED == resultCode) {    // 有効にされなかった
//                val show: Any = Toast.makeText(
//                        this,
//                        R.string.bluetooth_is_not_working,
//                        Toast.LENGTH_SHORT
//                ).show()
//                finish() // アプリ終了宣言
//                return
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }




    companion object {
        // 定数
        private const val REQUEST_ENABLEBLUETOOTH = 1 // Bluetooth機能の有効化要求時の識別コード
        val macAddr: String // MACアドレス
            get() {
                try {
                    val all: List<NetworkInterface> =
                        Collections.list(NetworkInterface.getNetworkInterfaces())
                    for (nif in all) {
                        if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                        val macBytes = nif.hardwareAddress ?: return ""
                        val res1 = StringBuilder()
                        for (b in macBytes) {
                            res1.append(String.format("%02X:", b))
                        }
                        if (res1.length > 0) {
                            res1.deleteCharAt(res1.length - 1)
                        }
                        return res1.toString()
                    }
                } catch (ex: Exception) {
                }
                return "02:00:00:00:00:00"
            }
    }


    // オプションメニュー作成時の処理
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    // オプションメニューのアイテム選択時の処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menuitem_search -> {
                val devicelistactivityIntent = Intent(this, DeviceListActivity::class.java)
                startActivityForResult(devicelistactivityIntent, REQUEST_CONNECTDEVICE)
                return true
            }
        }
        return false
    }
}