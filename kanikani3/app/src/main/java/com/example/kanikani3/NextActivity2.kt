package com.example.kanikani3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kanikani3.R
import kotlinx.android.synthetic.main.activity_next2.*

class NextActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next2)//xml遷移先
        val intent = getIntent()
        val gettext = intent.extras?.getString("data")
        //val gettext2 = intent.extras?.getString("data2")
        //val gettext3 = intent.extras?.getString("data3")


        textView_1.text = gettext //受け渡したいデータ
       // textView_2.text = gettext //受け渡したいデータ
     //   textView_3.text = gettext //受け渡したいデータ
    }
}