package com.example.kanikani3

//import android.R

//import android.R

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class DeviceListActivity : AppCompatActivity(), OnItemClickListener {
    internal class DeviceListAdapter(activity: Activity) : BaseAdapter() {
        private val mDeviceList: ArrayList<BluetoothDevice>
        private val mInflator: LayoutInflater

        // リストへの追加
        fun addDevice(device: BluetoothDevice) {
            if (!mDeviceList.contains(device)) {    // 加えられていなければ加える
                mDeviceList.add(device)
                notifyDataSetChanged() // ListViewの更新
            }
        }

        // リストのクリア
        fun clear() {
            mDeviceList.clear()
            notifyDataSetChanged() // ListViewの更新
        }

        override fun getCount(): Int {
            return mDeviceList.size
        }

        override fun getItem(position: Int): Any {
            return mDeviceList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        internal class ViewHolder {
            var deviceName: TextView? = null
            var deviceAddress: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convertView: View? = convertView
            val viewHolder: ViewHolder
            // General ListView optimization code.
            if (null == convertView) {
                convertView = mInflator.inflate(R.layout.listitem_device, parent, false)
                viewHolder = ViewHolder()
                viewHolder.deviceAddress = convertView.findViewById(R.id.textview_deviceaddress)
                viewHolder.deviceName = convertView.findViewById(R.id.textview_devicename)
                convertView.setTag(viewHolder)
            } else {
                viewHolder = convertView.getTag() as ViewHolder
            }
            val device = mDeviceList[position]
            val deviceName = device.name
            if (null != deviceName && 0 < deviceName.length) {
                viewHolder.deviceName!!.text = deviceName
            } else {
                viewHolder.deviceName?.setText(R.string.unknown_device)
            }
            viewHolder.deviceAddress!!.text = device.address
            return convertView
        }

        init {
            mDeviceList = ArrayList()
            mInflator = activity.layoutInflater
        }
    }

    // 定数
    private val REQUEST_ENABLEBLUETOOTH = 1 // Bluetooth機能の有効化要求時の識別コード
    val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
    val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"

    // メンバー変数
    private var mBluetoothAdapter // BluetoothAdapter : Bluetooth処理で必要
            : BluetoothAdapter? = null
    private var mDeviceListAdapter // リストビューの内容
            : DeviceListAdapter? = null
    private var mScanning = false // スキャン中かどうかのフラグ


    // ブロードキャストレシーバー
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action

            // Bluetooth端末発見
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                runOnUiThread { mDeviceListAdapter!!.addDevice(device!!) }
                return
            }
            // Bluetooth端末検索終了
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                runOnUiThread {
                    mScanning = false
                    // メニューの更新
                    invalidateOptionsMenu()
                }
                return
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        // 戻り値の初期化
        setResult(Activity.RESULT_CANCELED)

        // リストビューの設定
        mDeviceListAdapter = DeviceListAdapter(this) // ビューアダプターの初期化
        val listView: ListView = findViewById<View>(R.id.devicelist) as ListView // リストビューの取得
        listView.setAdapter(mDeviceListAdapter) // リストビューにビューアダプターをセット
        listView.setOnItemClickListener(this) // クリックリスナーオブジェクトのセット

        // Bluetoothアダプタの取得
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (null == mBluetoothAdapter) {    // デバイス（＝スマホ）がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish() // アプリ終了宣言
            return
        }
    }

    // 初回表示時、および、ポーズからの復帰時
    override fun onResume() {
        super.onResume()

        // デバイスのBluetooth機能の有効化要求
        requestBluetoothFeature()

        // ブロードキャストレシーバーの登録
        registerReceiver(mBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(mBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        // スキャン開始
        startScan()
    }

    // 別のアクティビティ（か別のアプリ）に移行したことで、バックグラウンドに追いやられた時
    override fun onPause() {
        super.onPause()

        // スキャンの停止
        stopScan()

        // ブロードキャストレシーバーの登録解除
        unregisterReceiver(mBroadcastReceiver)
    }

    // デバイスのBluetooth機能の有効化要求
    private fun requestBluetoothFeature() {
        if (mBluetoothAdapter!!.isEnabled) {
            return
        }
        // デバイスのBluetooth機能が有効になっていないときは、有効化要求（ダイアログ表示）
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH)
    }

    // 機能の有効化ダイアログの操作結果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLEBLUETOOTH -> if (Activity.RESULT_CANCELED == resultCode) {    // 有効にされなかった
                Toast.makeText(this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT).show()
                finish() // アプリ終了宣言
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // スキャンの開始
    private fun startScan() {
        // リストビューの内容を空にする。
        mDeviceListAdapter!!.clear()

        // スキャンの開始
        mScanning = true
        mBluetoothAdapter!!.startDiscovery() // 約 12 秒間の問い合わせのスキャンが行われる

        // メニューの更新
        invalidateOptionsMenu()
    }

    // スキャンの停止
    private fun stopScan() {
        // スキャンの停止
        mBluetoothAdapter!!.cancelDiscovery()
    }


    // リストビューのアイテムクリック時の処理
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // クリックされたアイテムの取得
        val device = mDeviceListAdapter!!.getItem(position) as BluetoothDevice
                ?: return
        // 戻り値の設定
        val intent = Intent()
        intent.putExtra(EXTRAS_DEVICE_NAME, device.name)
        intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.address)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // オプションメニュー作成時の処理
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_device_list, menu)
        if (!mScanning) {
            menu.findItem(R.id.menuitem_stop).setVisible(false)
            menu.findItem(R.id.menuitem_scan).setVisible(true)
            menu.findItem(R.id.menuitem_progress).setActionView(null)
        } else {
            menu.findItem(R.id.menuitem_stop).setVisible(true)
            menu.findItem(R.id.menuitem_scan).setVisible(false)
            menu.findItem(R.id.menuitem_progress).setActionView(R.layout.actionbar_indeterminate_progress)
        }
        return true
    }

    // オプションメニューのアイテム選択時の処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menuitem_scan -> startScan() // スキャンの開始
            R.id.menuitem_stop -> stopScan() // スキャンの停止
        }
        return true
    }


}
