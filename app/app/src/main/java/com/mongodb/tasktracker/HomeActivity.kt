package com.mongodb.tasktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.mongodb.tasktracker.databinding.ActivityHomeBinding
import com.mongodb.tasktracker.model.CourseInfo
import com.mongodb.tasktracker.model.SlotInfo
import io.realm.Realm
import com.mongodb.tasktracker.model.User
import io.realm.*
import io.realm.kotlin.where
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.Document
import org.bson.types.ObjectId

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var app: App
    private var studentName: String? = null
    private var studentEmail: String? = null
    private var departmentName: String? = null
    private var courseIds: List<ObjectId>? = null

    private var coursesInfo: List<CourseInfo>? = null
    private var slotsData: List<SlotInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo Realm
        Realm.init(this)
        val appConfiguration = AppConfiguration.Builder("finalproject-rujev").build()
        app = App(appConfiguration)  // Khởi tạo app

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận email từ Intent và thực hiện truy vấn dữ liệu
        intent.getStringExtra("USER_EMAIL")?.let {
            fetchStudentData(it)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.userInterface -> replaceFragment(InterfaceFragment())
                R.id.user -> replaceFragment(InforFragment())
                R.id.gear -> replaceFragment(GearFragment())
                R.id.shop -> replaceFragment(ShopFragment())
                else -> false
            }
            true
        }

        if (intent.getBooleanExtra("SHOW_INFOR_FRAGMENT", false)) {
            replaceFragment(InforFragment())
        } else {
            replaceFragment(InterfaceFragment())
        }
    }

    private fun fetchStudentData(userEmail: String) {
        val mongoClient = app.currentUser()?.getMongoClient("mongodb-atlas")
        val database = mongoClient?.getDatabase("finalProject")
        database?.getCollection("Students")?.findOne(Document("email", userEmail))?.getAsync { task ->
            if (task.isSuccess) {
                val studentDocument = task.get()

                studentName = studentDocument?.getString("name")
                this.studentEmail = studentDocument?.getString("email")
                studentDocument?.getObjectId("departmentId")?.let { fetchDepartmentData(it) }

                val enrolledCourses = studentDocument?.getList("enrolledCourses", ObjectId::class.java)
                if (enrolledCourses != null) {
                    fetchCoursesData(enrolledCourses)
                    // Thay đổi ở đây: Gọi fetchCoursesAndSlots thay vì fetchSlotsData
                    fetchCoursesAndSlots(enrolledCourses)
                }
            } else {
                Log.e("HomeActivity", "Error fetching student data: ${task.error}")
            }
        }
    }

    private fun fetchDepartmentData(departmentId: ObjectId) {
        val mongoClient = app.currentUser()!!.getMongoClient("mongodb-atlas")
        val database = mongoClient.getDatabase("finalProject")
        val departmentsCollection = database.getCollection("Departments")

        val query = Document("_id", departmentId)
        departmentsCollection.findOne(query).getAsync { task ->
            if (task.isSuccess) {
                val departmentDocument = task.get()
                if (departmentDocument != null) {
                    departmentName = departmentDocument.getString("name")
                } else {
                    Log.e("HomeActivity", "Không tìm thấy phòng ban với ID: $departmentId")
                }
            } else {
                Log.e("HomeActivity", "Lỗi khi truy vấn phòng ban: ${task.error}")
            }
        }
    }

    private fun fetchCoursesData(courseIds: List<ObjectId>) {
        val mongoClient = app.currentUser()!!.getMongoClient("mongodb-atlas")
        val database = mongoClient.getDatabase("finalProject")
        val coursesCollection = database.getCollection("Courses")
        val departmentsCollection = database.getCollection("Departments")

        val coursesInfo = mutableListOf<CourseInfo>()

        courseIds.forEach { courseId ->
            val query = Document("_id", courseId)

            coursesCollection.findOne(query).getAsync { task ->
                if (task.isSuccess) {
                    val courseDocument = task.get()
                    if (courseDocument != null) {
                        val title = courseDocument.getString("title")
                        val description = courseDocument.getString("description")
                        val departmentId = courseDocument.getObjectId("departmentId")
                        val credits = courseDocument.getInteger("credits", 0)

                        // Fetch department name
                        val deptQuery = Document("_id", departmentId)
                        departmentsCollection.findOne(deptQuery).getAsync { deptTask ->
                            if (deptTask.isSuccess) {
                                val departmentDocument = deptTask.get()
                                val departmentName = departmentDocument?.getString("name") ?: "Unknown"

                                // Add CourseInfo with department name
                                coursesInfo.add(CourseInfo(title, description, departmentName, credits))
                                checkAndPassCourses(coursesInfo, courseIds.size)
                            } else {
                                Log.e("HomeActivity", "Error fetching department: ${deptTask.error}")
                            }
                        }
                    }
                } else {
                    Log.e("HomeActivity", "Error fetching course: ${task.error}")
                }
            }
        }
    }

    private fun checkAndPassCourses(coursesInfo: List<CourseInfo>, totalCourses: Int) {
        if (coursesInfo.size == totalCourses) {
            passCoursesToFragment(coursesInfo)
        }
    }

    private fun passCoursesToFragment(coursesInfo: List<CourseInfo>) {
        this.coursesInfo = coursesInfo
        // Cập nhật InforFragment với dữ liệu mới
        val inforFragment = supportFragmentManager.findFragmentByTag("InforFragment") as? InforFragment
        inforFragment?.let {
            it.arguments = Bundle().apply {
                putString("name", studentName ?: "N/A")
                putString("email", studentEmail ?: "N/A")
                putString("department", departmentName ?: "N/A")
                putSerializable("courses", ArrayList(coursesInfo))
            }
            replaceFragment(it)
        }
    }

    private fun fetchCoursesAndSlots(courseIds: List<ObjectId>) {
        val mongoClient = app.currentUser()?.getMongoClient("mongodb-atlas")
        val database = mongoClient?.getDatabase("finalProject")
        val coursesCollection = database?.getCollection("Courses")
        val slotsCollection = database?.getCollection("Slots")
        val courseTitlesMap = mutableMapOf<String, String>()

        // Lấy thông tin từ Courses
        coursesCollection?.find(Document("_id", Document("\$in", courseIds)))?.iterator()?.getAsync { coursesTask ->
            if (coursesTask.isSuccess) {
                val courses = coursesTask.get()
                courses.forEach { course ->
                    val courseId = course.getObjectId("_id").toString()
                    val courseTitle = course.getString("title")
                    courseTitlesMap[courseId] = courseTitle ?: "Unknown Course"
                }

                // Khi có tất cả titles, lấy thông tin từ Slots và ghép nối
                slotsCollection?.find(Document("courseId", Document("\$in", courseIds)))?.iterator()?.getAsync { slotsTask ->
                    if (slotsTask.isSuccess) {
                        val slots = slotsTask.get()
                        val slotsList = mutableListOf<SlotInfo>()

                        slots.forEach { slot ->
                            val startTime = slot.getString("startTime")
                            val endTime = slot.getString("endTime")
                            val day = slot.getString("day")
                            val courseId = slot.getObjectId("courseId").toString()
                            val courseTitle = courseTitlesMap[courseId] ?: "Unknown Course"

                            slotsList.add(SlotInfo(startTime, endTime, day, courseId, courseTitle))
                        }

                        slotsData = slotsList
                        sendSlotsDataToInterfaceFragment()
                    } else {
                        Log.e("HomeActivity", "Error fetching slots data: ${slotsTask.error}")
                    }
                }
            } else {
                Log.e("HomeActivity", "Error fetching courses data: ${coursesTask.error}")
            }
        }
    }

    private fun sendSlotsDataToInterfaceFragment() {
        // Update InterfaceFragment with slots data if available
        slotsData?.let {
            val interfaceFragment = InterfaceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("slotsData", ArrayList(it))
                }
            }
            replaceFragment(interfaceFragment)
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        // Kiểm tra và cập nhật dữ liệu cho InterfaceFragment hoặc InforFragment nếu cần
        if (fragment is InterfaceFragment && slotsData != null) {
            fragment.arguments = Bundle().apply {
                putSerializable("slotsData", ArrayList(slotsData))
            }
        } else if (fragment is InforFragment && coursesInfo != null) {
            fragment.arguments = Bundle().apply {
                putString("name", studentName ?: "N/A")
                putString("email", studentEmail ?: "N/A")
                putString("department", departmentName ?: "N/A")
                putSerializable("courses", ArrayList(coursesInfo))
            }
        }

        // Thực hiện thay thế Fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment, fragment.javaClass.simpleName)
            commit()
        }
    }

}
