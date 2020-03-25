package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.christianfrom.mandatoryassignment.Model.Room;

public class SingleRoomActivity extends AppCompatActivity {
    public static final String ROOM = "room";
    private static final String LOG_TAG = "ROOMS";
    private Room room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra(ROOM);

        Log.d(LOG_TAG, room.toString());


        TextView id = findViewById(R.id.singleRoomIdText);
        id.setText("ID: " + room.getId().toString());

        TextView name = findViewById(R.id.singleRoomNameText);
        name.setText("Name: " + room.getName());

        TextView description = findViewById(R.id.singleRoomDescriptionText);
        description.setText("Description: " + room.getDescription());

        TextView capacity = findViewById(R.id.singleRoomCapacityText);
        capacity.setText("Capacity: " + room.getCapacity().toString());

        TextView remarks = findViewById(R.id.singleRoomRemarksText);
        remarks.setText("Remarks: " + room.getRemarks());
    }

    public void backButtonClicked(View view) {
        finish();
    }
}
