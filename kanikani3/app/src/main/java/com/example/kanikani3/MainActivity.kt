package com.example.kanikani3

//import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.net.NetworkInterface
import java.util.*


class MainActivity : AppCompatActivity() {

    // メンバー変数
    private var mBluetoothAdapter // BluetoothAdapter : Bluetooth処理で必要
            : BluetoothAdapter? = null
    var pref: SharedPreferences? = null
    var b : String? = null
    var n : String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var pref: SharedPreferences? = null
        if (pref != null) {
            b = pref.getString("userdata", "")
        };
        if (pref != null) n = pref.getString("twitterdata", "");



        // Bluetoothアダプタの取得
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish() // アプリ終了宣言
            return
        }

    }
   /* override fun onResume() {
        super.onResume()
        val e = pref!!.edit()
        e.putString("stringdata", b)
        e.putString("twitterdata", n)
        e.commit()
    }*/




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