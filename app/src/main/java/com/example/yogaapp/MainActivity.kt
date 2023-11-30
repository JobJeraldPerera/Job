package com.example.yogaapp

import DatabaseHelper
import android.app.DatePickerDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// Adjust package and class name










import com.example.yogaapp.ui.theme.YogaappTheme
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : ComponentActivity() {





    // Replace with your adapter class name

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassListAdapter
    private var classList = mutableListOf<ClassModel>()

    private lateinit var dbHelper: DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        // Replace with the identifier for your login.xml layout
        dbHelper = DatabaseHelper(this)



        val loginButton: Button = findViewById(R.id.loginButton)
        val userIDEditText: EditText = findViewById(R.id.editTextUserID)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)




        loginButton.setOnClickListener {
            val enteredUsername = userIDEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            if (isValidCredentials(enteredUsername, enteredPassword)) {
                // Display success message for valid credentials
                showToast("Login Successful")
                navigateTohome()
                // Navigate to the next screen or perform necessary actions for successful login
            } else {
                // Display error message for invalid credentials
                showToast("Invalid credentials")
            }


        }


    }

    private fun isValidCredentials(enteredUsername: String, enteredPassword: String): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.USER_NAME, DatabaseHelper.USER_PASSWORD)
        val selection = "${DatabaseHelper.USER_NAME}=? AND ${DatabaseHelper.USER_PASSWORD}=?"
        val selectionArgs = arrayOf(enteredUsername, enteredPassword)

        val cursor: Cursor = db.query(
            DatabaseHelper.DATABASE_TABLE_USERS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val isValid: Boolean = cursor != null && cursor.count > 0

        // Close the cursor and database to release resources
        cursor.close()
        db.close()

        return isValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateTohome() {
        setContentView(R.layout.home)
        val uploadButton: Button = findViewById(R.id.uploadButton)
        val btnAddClass: Button = findViewById(R.id.btnAddClass)
        val btnShowClasses: Button = findViewById(R.id.btnShowClasses)

        uploadButton.setOnClickListener {
            // Call the function to retrieve classList from the database or wherever it's stored
            val userId = "001285364" // Replace with actual user ID
            val classList = getClassListFromDatabase() // Implement this function to fetch class list

            // Create JSON payload
            val jsonPayload = createJSONPayload(userId, classList)

            // Upload JSON to the web service
            GlobalScope.launch(Dispatchers.IO) {
                uploadToWebService(jsonPayload)
            }
        }




        btnAddClass.setOnClickListener {
            navigateToAddClass()
        }

        btnShowClasses.setOnClickListener {
            setContentView(R.layout.classlist)
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Fetch data from COURSES_TABLE
            loadClassesFromDatabase()
        }


    }

    private fun getClassListFromDatabase(): List<ClassModel> {
        val classList = mutableListOf<ClassModel>()

        // Assuming dbHelper is your DatabaseHelper
        val db = dbHelper.readableDatabase

        // Query your database to retrieve class information
        val cursor = db.query(
            DatabaseHelper.DATABASE_TABLE_COURSES,
            null,
            null,
            null,
            null,
            null,
            null
        )

        // Extract data from the cursor and populate ClassModel objects
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COURSE_ID))
                val dayOfWeek = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DAY_OF_WEEK))
                val time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME))
                val capacity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CAPACITY))
                val duration = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DURATION))
                val price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.PRICE))
                val type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TYPE))
                val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION))

                // Create ClassModel object and add it to the list
                val classModel = ClassModel(
                    id, dayOfWeek, time, capacity, duration, price, type, description
                )
                classList.add(classModel)
            } while (cursor.moveToNext())
        }

        // Close cursor and database connection
        cursor?.close()
        db.close()

        return classList
    }



    private suspend fun uploadToWebService(jsonPayload: String) {
        val url = "https://stuiis.cms.gre.ac.uk/COMP1424CoreWS/comp1424cw" // Replace with your URL
        val client = HttpClient(Android) {
            // Configure the HTTP client if needed
        }

        try {
            val response: HttpResponse = client.post(url) {
                body = jsonPayload
                headers.append("Content-Type", "application/json")
                headers.append("Accept", "application/json")
                // Add other headers if required
            }

            // Check the response status code
            if (response.status.isSuccess()) {
                val responseBody = response.readText()
                // Handle the successful response
                // You can parse responseBody if it's JSON or handle it accordingly
            } else {
                // Handle unsuccessful response
                // For example:
                val errorMessage = "Failed to upload data. Status code: ${response.status}"
                // Display an error message to the user or log it
            }
        } catch (e: Exception) {
            // Handle exceptions if any occurred during the request
            val errorMessage = "Exception: ${e.message}"
            // Display an error message to the user or log it
        } finally {
            client.close() // Close the client when finished
        }
    }


    private fun navigateToClassList() {


    }

    private fun loadClassesFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val classList = mutableListOf<ClassModel>()

            val cursor = db.query(
                DatabaseHelper.DATABASE_TABLE_COURSES,
                null,
                null,
                null,
                null,
                null,
                null
            )

            withContext(Dispatchers.Main) {
                classList.clear() // Clear the list before updating it
                with(cursor) {
                    while (moveToNext()) {
                        val id = getInt(getColumnIndex(DatabaseHelper.COURSE_ID))
                        val dayOfWeek = getString(getColumnIndex(DatabaseHelper.DAY_OF_WEEK))
                        val time = getString(getColumnIndex(DatabaseHelper.TIME))
                        val capacity = getInt(getColumnIndex(DatabaseHelper.CAPACITY))
                        val duration = getString(getColumnIndex(DatabaseHelper.DURATION))
                        val price = getDouble(getColumnIndex(DatabaseHelper.PRICE))
                        val type = getString(getColumnIndex(DatabaseHelper.TYPE))
                        val description = getString(getColumnIndex(DatabaseHelper.DESCRIPTION))

                        // Create a ClassModel object and add it to the classList
                        val classModel = ClassModel(id, dayOfWeek, time, capacity, duration, price, type, description)
                        classList.add(classModel)
                    }
                }

                cursor.close()
                db.close()

                // Update the RecyclerView with the retrieved data
                updateRecyclerView(classList)
            }
        }
    }

    private fun updateRecyclerView(classList: List<ClassModel>) {
        adapter = ClassListAdapter(
            classList.toMutableList(),
            { id ->
                adapter.deleteCourse(id)
            },
            dbHelper,
            { selectedClass ->
                // Handle item click here, switch to a different XML layout within the same activity
                Log.d("ItemClick", "Single click occurred")

            },
            { selectedClass ->
                // Handle item click here, switch to a different XML layout within the same activity
                navigateToAddClassInstance()
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun navigateToDisplayClassInstances(selectedClass: ClassModel) {
        // Navigate to the display_class_instances_layout
        val intent = Intent(this, DisplayClassInstancesActivity::class.java)
        intent.putExtra("class_model", selectedClass)
        startActivity(intent)
    }
    private fun navigateToAddClassInstance() {
        // Navigate to the add_class_instance_layout
        setContentView(R.layout.add_class_instance_layout)
        // Additional logic for add_class_instance_layout if needed
        val addInstanceButton: Button = findViewById(R.id.buttonAddInstance)

        val buttonSelectDate: Button = findViewById(R.id.buttonSelectDate)

        // Add an onClickListener to the "Select Date" button
        buttonSelectDate.setOnClickListener {
            // Call the function to show the DatePickerDialog
            showDatePickerDialog()
        }

        addInstanceButton.setOnClickListener {
            val editTextDate: EditText = findViewById(R.id.editTextDate)
            val editTextTeacher: EditText = findViewById(R.id.editTextTeacher)

            val classId: Long = 1 // Replace with the actual class ID
            val date = editTextDate.text.toString().trim() // Retrieve and trim the date text
            val teacher = editTextTeacher.text.toString().trim()
            // Add the class instance with the teacher to the database
            dbHelper.insertClassInstanceWithTeacher(classId, date, teacher)

            if (date.isNotEmpty() && teacher.isNotEmpty()) {
                // Uncomment this code to add an instance when both fields are filled
                // dbHelper.insertClassInstanceWithTeacher(classId, date, teacher)
                Toast.makeText(this, "Class instance added", Toast.LENGTH_SHORT).show()
            } else {
                if (date.isEmpty()) {
                    Toast.makeText(this, "Please fill the date field", Toast.LENGTH_SHORT).show()
                }
                if (teacher.isEmpty()) {
                    Toast.makeText(this, "Please fill the teacher field", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val buttonSelectDate: Button = findViewById(R.id.buttonSelectDate)
        val editTextDate: EditText = findViewById(R.id.editTextDate)

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Handle selected date
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                editTextDate.setText(selectedDate) // Set selected date in EditText
                Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
            },
            // Set the default date to display when the dialog opens
            2023, 10, 1 // Replace with your desired default year, month, and day
        )

        datePicker.show()
    }








    private fun navigateToAddClass() {
        setContentView(R.layout.addclass)

        val submitButton: Button = findViewById(R.id.submitButton)
        val editTextDayOfWeek: EditText = findViewById(R.id.editTextDayOfWeek)
        val editTextTime: EditText = findViewById(R.id.editTextTime)
        val editTextCapacity: EditText = findViewById(R.id.editTextCapacity)
        val editTextDuration: EditText = findViewById(R.id.editTextDuration)
        val editTextPrice: EditText = findViewById(R.id.editTextPrice)
        val editTextType: EditText = findViewById(R.id.editTextType)
        val editTextDescription: EditText = findViewById(R.id.editTextDescription)

        submitButton.setOnClickListener {
            val dayOfWeek = editTextDayOfWeek.text.toString()
            val time = editTextTime.text.toString()
            val capacity = editTextCapacity.text.toString().toIntOrNull() ?: 0
            val duration = editTextDuration.text.toString()
            val price = editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
            val type = editTextType.text.toString()
            val description = editTextDescription.text.toString()

            if (validateInputs(

                    dayOfWeek,
                    time.toString(),
                    capacity.toString(),
                    duration,
                    price.toString(),
                    type,
                    description
                )
            ) {
                saveToDatabase(dayOfWeek, time, capacity, duration, price, type, description)
            } else {
                showToast("Please fill all fields")
            }
        }
    }

    private fun validateInputs(vararg fields: String): Boolean {
        return fields.all { it.isNotEmpty() }
    }

    private fun saveToDatabase(
        dayOfWeek: String,
        time: String,
        capacity: Int,
        duration: String,
        price: Double,
        type: String,
        description: String
    ) {
        // Save data to the database (COURSES_TABLE) using dbHelper
        dbHelper.insertCourse(dayOfWeek, time, capacity, duration, price, type, description)

        showToast("Data saved successfully")
    }


    fun createJSONPayload(userId: String, classList: List<ClassModel>): String {
        val payload = JSONObject()
        payload.put("userId", "001285364")

        val detailList = JSONArray()
        for (classModel in classList) {
            val courseObject = JSONObject()
            courseObject.put("dayOfWeek", classModel.dayOfWeek)
            courseObject.put("timeOfDay", classModel.time)
            courseObject.put("capacity", classModel.capacity)
            courseObject.put("duration", classModel.duration)
            courseObject.put("price", classModel.price)
            courseObject.put("type", classModel.type)
            courseObject.put("description", classModel.description)

            // Add other fields if needed

            detailList.put(courseObject)
        }

        payload.put("detailList", detailList)

        return payload.toString()
    }








    @Composable
    fun LoginScreen() {
        // Your login screen UI components go here
        // Replace this placeholder Text with your login form components
        Text(text = "Login Screen")

    }

    @Preview(showBackground = true)
    @Composable
    fun LoginPreview() {
        YogaappTheme {
            LoginScreen()
        }
    }
}

private fun Intent.putExtra(s: String, selectedClass: ClassModel) {

}




