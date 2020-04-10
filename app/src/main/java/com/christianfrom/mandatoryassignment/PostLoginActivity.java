package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.christianfrom.mandatoryassignment.Model.Reservation;
import com.christianfrom.mandatoryassignment.Model.Room;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.ReservationRESTService;
import com.christianfrom.mandatoryassignment.REST.RoomRESTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "TEST";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        TextView welcomeEditText = findViewById(R.id.welcomeTextView);
        welcomeEditText.setText("Welcome " + user.getEmail());
        getAllRooms();
    }

    private void getAllRooms() {
        RoomRESTService rrs = ApiUtils.getRoomsService();
        Call<List<Room>> getAllRoomsCall = rrs.getAllRooms();
        TextView messageView = findViewById(R.id.mainMessageTextView);

        getAllRoomsCall.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful()) {
                    List<Room> allRooms = response.body();
                    Log.d(LOG_TAG, allRooms.toString());
                    populateRecyclerView(allRooms);
                } else {
                    String message = "Problem " + response.code() + " " + response.message();
                    Log.d(LOG_TAG, message);
                    messageView.setText(message);
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                messageView.setText(t.getMessage());
            }
        });

    }

    private void populateRecyclerView(List<Room> allRooms)
    {
        RecyclerView recyclerView = findViewById(R.id.PostLoginRoomsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(allRooms);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            Room room = (Room) item;
            Intent intent = new Intent(PostLoginActivity.this, SingleRoomActivity.class);
            intent.putExtra(SingleRoomActivity.ROOM, room);
            startActivity(intent);
        });
    }



    //Todo tilføj en appbar



    public void logoutFloatButtonPressed(View view) {
        mAuth.getInstance().signOut();
        finish();
        Toast.makeText(this, "You have now logged out...", Toast.LENGTH_SHORT).show();
    }
}
