package com.example.yogaapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ShowInstancesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showinstances)

        // Fetch necessary data based on the class ID passed from the ClassListAdapter
        val classId = intent.getLongExtra("class_id", -1)
        // Use the classId to fetch instances data from your database or perform any required actions
        // ...

        // Your code to display instances or handle the selected class's instances
        // ...
    }
}