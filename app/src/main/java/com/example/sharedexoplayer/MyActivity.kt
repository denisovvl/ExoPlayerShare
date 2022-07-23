package com.example.sharedexoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedexoplayer.databinding.MyActivityBinding

class MyActivity : AppCompatActivity() {

    private lateinit var binding: MyActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.conteiner, MyFragmentList(), "MyFragmentList")
                commit()
            }
        }
    }



}