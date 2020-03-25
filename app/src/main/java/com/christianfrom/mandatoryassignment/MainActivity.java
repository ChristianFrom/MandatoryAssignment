package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.christianfrom.mandatoryassignment.Model.Room;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.RoomRESTService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
private static final String LOG_TAG = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAllRooms();
    }

    public void loginFloatButtonPressed(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }




    private void getAllRooms() {
        RoomRESTService rrs = ApiUtils.getRoomsService();
        Call<List<Room>> getAllRoomsCall = rrs.getAllRooms();
        TextView messageView = findViewById(R.id.mainMessageTextView);

        messageView.setText("");
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
        RecyclerView recyclerView = findViewById(R.id.mainRoomsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(allRooms);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            Room room = (Room) item;
            Intent intent = new Intent(MainActivity.this, SingleRoomActivity.class);
            intent.putExtra(SingleRoomActivity.ROOM, room);
            startActivity(intent);
        });
    }

}
