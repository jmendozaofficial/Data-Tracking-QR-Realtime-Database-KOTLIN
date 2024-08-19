package com.example.ltbmodatatrackerv2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ltbmodatatrackerv2.databinding.ActivityQrsessionFullListBinding

class QRSessionFullList : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityQrsessionFullListBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.content_qrsession_full_list)
        val qrlistview = findViewById<ListView>(R.id.qrsessionlist)

        val qrResultListArray = intent.getStringArrayExtra("qrResultListArray") // coverts to array

        if (qrResultListArray != null) {
            for (item in qrResultListArray) {
                val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(
                    this, android.R.layout.simple_list_item_1,
                    qrResultListArray
                )
                qrlistview.adapter = arrayAdapter
                qrlistview.setOnItemClickListener {
                        adapterView, view, i, l -> Toast.makeText(this, "Item Selected " + qrResultListArray[i],
                    Toast.LENGTH_LONG).show()
                }
            }
        }

    }


}
