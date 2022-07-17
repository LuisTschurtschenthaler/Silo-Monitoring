package com.layer8studios.silomonitoring.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.layer8studios.silomonitoring.databinding.ActivityCreateSiloBinding


class CreateSiloActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSiloBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateSiloBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}