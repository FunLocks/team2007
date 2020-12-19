package com.example.kanikani3

//import android.R


//import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.util.*


class MainActivity : AppCompatActivity() {
    // 定数
    private val REQUEST_ENABLEBLUETOOTH = 1 // Bluetooth機能の有効化要求時の識別コード
    private val REQUEST_CONNECTDEVICE = 2 // デバイス接続要求時の識別コード


    // メンバー変数
    private var mBluetoothAdapter // BluetoothAdapter : Bluetooth処理で必要
            : BluetoothAdapter? = null
    var pref: SharedPreferences? = null
    private var mDeviceAddress = "" // デバイスアドレス
    var otherMacAdr = "unchi" //他のユーザーのmacアドレス（bluetoothで受け取る）
    val TAG = ""
     var b: String? = null
    //var b1 : String? =null

    var c: String? = null
    var d: String? = null

    //var inputdata = arrayOfNulls<String>(size = 3)


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //var b = findViewById<EditText>(R.id.EditText_UserName).toString()
        var b = findViewById<EditText>(R.id.EditText_UserName)
        var c = findViewById<EditText>(R.id.EditText_TwitterName)
        var d = findViewById<EditText>(R.id.editTextTextMultiLine_comment)
        var pref: SharedPreferences? = null

//        if (pref != null) c = pref.getString("Twitter_Id", "").toString()
//        if (pref != null) d = pref.getString("Comment", "").toString()

        TransButton.setOnClickListener {

           // if(pref != null)  b = pref.getString("Username","").toString()
            var b = b.text.toString()
            var c = c.text.toString()
            var d = d.text.toString()
            val intent = Intent(application, NextActivity2::class.java) //nextにわた
            //データ送信
            // Access a Cloud Firestore instance from your Activity
            val db = FirebaseFirestore.getInstance()
            val suretigai = db.collection("suretigai")

            val userData = hashMapOf( //自分のデータを送信
                "Username" to b,
                "Twitter_Id" to c,
                "Comment" to d
            )
            suretigai.document(macAddr).set(userData)



            //データ送信ここまで

            // 他ユーザーのデータ取得
            var docRef = db.collection("suretigai").document(otherMacAdr)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        // 表示処理を書く
                        var moji = document.data.toString()
                        //var mj_nagasa = moji.length
                        var s_usrnm = moji.indexOf("Username=")
                        var s_twid = moji.indexOf("Twitter_Id=")

                        var com = moji.substring(9, s_twid - 2)
                        var tID = moji.substring(s_twid + 11, s_usrnm - 2)
                        var usrnm = moji.substring(s_usrnm + 9, moji.length - 1)

//                        intent.putExtra("data", usrnm)
//                        intent.putExtra("data2", tID)
//                        intent.putExtra("data3", com)
                        //findViewById<TextView>(R.id.textView6).text = document.data.toString()
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

            startActivity(intent)
        }



        // Bluetoothアダプタの取得
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish() // アプリ終了宣言
            return
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
            e.putString("comentdata", d)
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
                    mDeviceAddress =
                        data.getStringExtra(DeviceListActivity.Foo.EXTRAS_DEVICE_ADDRESS)!!
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
