package com.example.kanikani3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kanikani3.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_next2.*

class NextActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next2)//xml遷移先
        val db = FirebaseFirestore.getInstance()
        var otherMacAdr = "unchi"
        val TAG = ""
        val intent = getIntent()

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

                    var com = moji.substring(9, s_twid-2)
                    var tID = moji.substring(s_twid+11, s_usrnm-2)
                    var usrnm = moji.substring(s_usrnm+9, moji.length-1)

//                    usrnm = intent.extras?.getString("data").toString()
//                    tID = intent.extras?.getString("data").toString()
//                    com = intent.extras?.getString("data").toString()

//                    textView_1.text = com //受け渡したいデータ
//                    textView_2.text = usrnm //受け渡したいデータ
//                    textView_3.text = tID //受け渡したいデータ

                    //findViewById<TextView>(R.id.textView6).text = document.data.toString()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

//        val gettext = intent.extras?.getString("data")
//        val gettext2 = intent.extras?.getString("data2")
//        val gettext3 = intent.extras?.getString("data3")


//        textView_1.text = com //受け渡したいデータ
//        textView_2.text = gettext2 //受け渡したいデータ
//        textView_3.text = gettext3 //受け渡したいデータ
    }
}