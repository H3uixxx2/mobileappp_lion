package com.mongodb.tasktracker

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import org.bson.Document
import at.favre.lib.crypto.bcrypt.BCrypt
import org.bson.AbstractBsonWriter


class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var app: App
    private lateinit var rememberMeCheckBox: CheckBox

    private val prefsName = "Final_Project"
    private val usernameKey = "username"
    private val passwordKey = "password"
    private val rememberMeKey = "rememberMe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Khởi tạo Realm
        Realm.init(this)
        val appConfiguration = AppConfiguration.Builder("finalproject-rujev").build()
        app = App(appConfiguration)

        // Di chuyển đoạn mã này vào trong loginAsync
        app.loginAsync(Credentials.emailPassword("mobile@gmail.com", "123123")) { result ->
            if (result.isSuccess) {
                Log.v("User", "Đăng nhập thành công!")
                // Đoạn mã xử lý sau khi đăng nhập thành công
                fetchData()
            } else {
                Log.e("User", "Đăng nhập thất bại: ${result.error}")
            }
        }

        // Ánh xạ view
        usernameEditText = findViewById(R.id.input_username)
        passwordEditText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.button_login)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        checkSavedCredentials()

        // Thiết lập sự kiện click cho nút đăng nhập
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            Login(username, password)
        }
    }

    private fun fetchData() {
        val user = app.currentUser()
        val mongoClient = user!!.getMongoClient("mongodb-atlas")
        val database = mongoClient.getDatabase("finalProject")
        val collection = database.getCollection("Users")

        collection.find().iterator().getAsync { task ->
            if (task.isSuccess) {
                val results = task.get()
                while (results.hasNext()) {
                    val document = results.next()
                    Log.v("Data", "Tìm thấy document: ${document.toJson()}")
                }
            } else {
                Log.e("Data", "Lỗi khi lấy dữ liệu: ${task.error}")
            }
        }
    }

    //Check app đã được đăng nhập trước đó và nhớ Acc chưa
    private fun checkSavedCredentials() {
        val settings = getSharedPreferences(prefsName, MODE_PRIVATE)
        val savedUsername = settings.getString(usernameKey, "")
        val savedPassword = settings.getString(passwordKey, "")
        val rememberMe = settings.getBoolean(rememberMeKey, false)

        if (rememberMe && !savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
            rememberMeCheckBox.isChecked = true
        }
    }

    private fun Login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_LONG).show()
            return
        }

        val mongoClient = app.currentUser()!!.getMongoClient("mongodb-atlas")
        val database = mongoClient.getDatabase("finalProject")
        val collection = database.getCollection("Users")

        //tạo query với email
        val query = Document("details.email", email)

        collection.findOne(query).getAsync { task ->
            if (task.isSuccess) {
                val userDocument = task.get()
                if (userDocument != null) {
                    val storedPasswordHash = userDocument.getString("password")
                    val result = BCrypt.verifyer().verify(password.toCharArray(), storedPasswordHash)
                    if (result.verified) {
                        Log.v("Login", "Đăng nhập thành công!")
                        rememberAccount(email, password, rememberMeCheckBox.isChecked)

                        val homeIntent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("USER_EMAIL", email)
                            putExtra("SHOW_INFOR_FRAGMENT", false)
                        }
                        startActivity(homeIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Error during login: ${task.error.errorMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun rememberAccount(username: String, password: String, rememberMe: Boolean) {
        val settings = getSharedPreferences(prefsName, MODE_PRIVATE)
        settings.edit().apply {
            if (rememberMe) {
                putString(usernameKey, username)
                putString(passwordKey, password)
            } else {
                remove(usernameKey)
                remove(passwordKey)
            }
            putBoolean(rememberMeKey, rememberMe)
            apply()
        }
    }
}
