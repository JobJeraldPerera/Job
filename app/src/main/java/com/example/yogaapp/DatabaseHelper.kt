import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
                const val DATABASE_NAME = "MY_APP"
        const val DATABASE_VERSION = 2
        const val DATABASE_TABLE_USERS = "USERS"
        const val DATABASE_TABLE_COURSES = "COURSES"
                 const val TABLE_CLASS_INSTANCES = "class_instances"

        const val USER_ID = "_ID"
        const val USER_NAME = "user_name"
        const val USER_PASSWORD = "user_password"

        const val COURSE_ID = "_ID"
        const val DAY_OF_WEEK = "day_of_week"
        const val TIME = "time"
        const val CAPACITY = "capacity"
        const val DURATION = "duration"
        const val PRICE = "price"
        const val TYPE = "type"
        const val DESCRIPTION = "description"

                const val COLUMN_ID = "id"
                 const val COLUMN_CLASS_ID = "class_id"
                 const val COLUMN_DATE = "date"
                 const val COLUMN_TEACHER = "teacher"

                const val COURSE_COLUMNS = "$COURSE_ID, $DAY_OF_WEEK, $TIME, $CAPACITY, $DURATION, $PRICE, $TYPE, $DESCRIPTION"
        }

        override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUsersTableQuery)
        db.execSQL(createCoursesTableQuery)
        insertAdminUsers(db)
                Log.d("DatabaseHelper", "Database tables created")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE_COURSES")
        onCreate(db)
                Log.d("DatabaseHelper", "Database upgraded")
        }

private val createUsersTableQuery =
        "CREATE TABLE $DATABASE_TABLE_USERS (" +
        "$USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "$USER_NAME TEXT NOT NULL, " +
        "$USER_PASSWORD TEXT);"

private val createCoursesTableQuery =
        "CREATE TABLE $DATABASE_TABLE_COURSES (" +
        "$COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "$DAY_OF_WEEK TEXT NOT NULL, " +
        "$TIME TEXT NOT NULL, " +
        "$CAPACITY INTEGER NOT NULL, " +
        "$DURATION TEXT NOT NULL, " +
        "$PRICE REAL NOT NULL, " +
        "$TYPE TEXT NOT NULL, " +
        "$DESCRIPTION TEXT);"

private fun insertAdminUsers(db: SQLiteDatabase) {
        val admin1 = ContentValues().apply {
        put(USER_NAME, "job")
        put(USER_PASSWORD, "password1")
        }
        db.insert(DATABASE_TABLE_USERS, null, admin1)

        val admin2 = ContentValues().apply {
        put(USER_NAME, "admin2")
        put(USER_PASSWORD, "password2")
        }
        db.insert(DATABASE_TABLE_USERS, null, admin2)
        }

        fun insertCourse(

        dayOfWeek: String,
        time: String,
        capacity: Int,
        duration: String,
        price: Double,
        type: String,
        description: String
        ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
        put(DAY_OF_WEEK, dayOfWeek)
        put(TIME, time)
        put(CAPACITY, capacity)
        put(DURATION, duration)
        put(PRICE, price)
        put(TYPE, type)
        put(DESCRIPTION, description)
        }
        return db.insert(DATABASE_TABLE_COURSES, null, values)
        }

        fun insertUser(userName: String, userPassword: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
        put(USER_NAME, userName)
        put(USER_PASSWORD, userPassword)
        }
        return db.insert(DATABASE_TABLE_USERS, null, values)
        }

        fun deleteCourse(courseId: Long): Int {
                val db = this.writableDatabase
                val whereClause = "$COURSE_ID = ?"
                val whereArgs = arrayOf(courseId.toString())
                val deletedRows = db.delete(DATABASE_TABLE_COURSES, whereClause, whereArgs)
                db.close()
                return deletedRows
        }

        // Inside your DatabaseHelper class
        fun insertClassInstanceWithTeacher(classId: Long, date: String, teacher: String) {
                val db = this.writableDatabase
                val contentValues = ContentValues()
                contentValues.put(COLUMN_CLASS_ID, classId)
                contentValues.put(COLUMN_DATE, date)
                contentValues.put(COLUMN_TEACHER, teacher)

                db.insert(TABLE_CLASS_INSTANCES, null, contentValues)
                db.close()
        }




        // Add other CRUD operations as needed
        }
