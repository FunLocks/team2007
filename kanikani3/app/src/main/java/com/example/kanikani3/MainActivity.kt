package com.example.kanikani3

//import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mBluetoothAdapter // BluetoothAdapter : Bluetooth処理で必要
            : BluetoothAdapter? = null
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
// Access a Cloud Firestore instance from your Activity
            val db = FirebaseFirestore.getInstance()



        var pref: SharedPreferences? = null
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
    }

        override fun onResume() {
            super.onResume()
            if(pref!=null) {
                val e = pref!!.edit()
                e.putString("Stingdata", b)
                e.putString("twitterdata", c)
                e.putString("comentdata" ,d)
                e.commit()
            }
        }


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
}
