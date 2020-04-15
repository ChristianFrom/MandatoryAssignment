package com.christianfrom.mandatoryassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.christianfrom.mandatoryassignment.Model.Room
import com.christianfrom.mandatoryassignment.REST.ApiUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_start_page_kotlin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class StartPageKotlin : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page_kotlin)

        floatingButtonLogin.setOnClickListener {
            var intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        userLoggedIn()
        getAllRooms()

    }


    fun populateRecyclerView(allRooms: List<Room>) {
        mainRoomsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewSimpleAdapter(allRooms)
        mainRoomsRecyclerView.adapter = adapter
        adapter.setOnItemClickListener { view, position, item ->
            run {
                val room = item
                val intent = Intent(this, SingleRoomActivity::class.java)
                intent.putExtra(SingleRoomActivity.ROOM, room as Serializable) // Hold øje med
                startActivity(intent)
            }
        }
    }

    private fun userLoggedIn() {
        if (user != null) {
            floatingButtonLogin.hide()
            val intent = Intent(this, PostLoginActivity::class.java)
            startActivity(intent)
        } else {
            floatingButtonLogin.show()
        }
    }


    private fun getAllRooms() {
        val roomRESTService = ApiUtils.getRoomsService()
        val call = roomRESTService.allRooms

        call.enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    val allRooms = response.body()
                    if (allRooms != null) {
                        populateRecyclerView(allRooms)
                    }
                } else {
                    val message = "Problem " + response.code() + " " + response.message()
                    Log.d("error", message)
                    mainMessageTextView.text = message
                }
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                mainMessageTextView.text = t.message
            }
        })

    }
}
