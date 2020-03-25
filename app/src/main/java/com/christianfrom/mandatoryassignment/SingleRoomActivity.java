package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.christianfrom.mandatoryassignment.Model.Reservation;
import com.christianfrom.mandatoryassignment.Model.Room;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.ReservationRESTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleRoomActivity extends AppCompatActivity {
    public static final String ROOM = "room";
    private static final String LOG_TAG = "ROOMS";
    private Room room;
    private FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);
        getSingleRoomReservations();

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra(ROOM);

        Log.d(LOG_TAG, room.toString());

        TextView id = findViewById(R.id.singleRoomIdText);
        id.setText(room.getId().toString());

        TextView name = findViewById(R.id.singleRoomNameText);
        name.setText(room.getName());

        TextView description = findViewById(R.id.singleRoomDescriptionText);
        description.setText(room.getDescription());

        TextView capacity = findViewById(R.id.singleRoomCapacityText);
        capacity.setText(room.getCapacity().toString());

        TextView remarks = findViewById(R.id.singleRoomRemarksText);

            if (room.getRemarks() == null)
            {
                remarks.setText("No remarks for this room.");
            }
            else {
                remarks.setText(room.getRemarks());
            }
    }


    public void userLoggedIn() {
        if (user != null) {
            Log.d("user", user.getEmail());
            Button bookRoomButton = findViewById(R.id.bookRoomButton);
            bookRoomButton.setEnabled(true);
        }
        else{
            TextView errorText = findViewById(R.id.bookRoomErrorText);
            errorText.setVisibility(View.VISIBLE);
        }
    }

     private void getSingleRoomReservations() {
         Intent intent = getIntent();
         room = (Room) intent.getSerializableExtra(ROOM);
         ReservationRESTService rrs = ApiUtils.getReservationsService();
         Call<List<Reservation>> getSingleRoomsReservationsCall = rrs.getRoomReservations(room.getId());
         userLoggedIn();
         getSingleRoomsReservationsCall.enqueue(new Callback<List<Reservation>>() {
             @Override
             public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                 if (response.isSuccessful()) {
                     List<Reservation> allSingleRoomReservations = response.body();
                     if (allSingleRoomReservations.toString() == "[]") {
                         TextView errorText = findViewById(R.id.singleRoomRecyclerText);
                         errorText.setVisibility(View.VISIBLE);
                     }
                     else
                     {
                         Log.d(LOG_TAG, allSingleRoomReservations.toString());
                         populateRecyclerView(allSingleRoomReservations);
                     }
                 } else {
                     String message = "Problem " + response.code() + " " + response.message();
                     Log.d(LOG_TAG, message);
                 }
             }

             @Override
             public void onFailure(Call<List<Reservation>> call, Throwable t) {
                 Log.e(LOG_TAG, t.getMessage());
             }
         });
     }


    private void populateRecyclerView(List<Reservation> allReservations)
    {
        RecyclerView recyclerView = findViewById(R.id.singleRoomRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(allReservations);
        recyclerView.setAdapter(adapter);
    }


    public void backButtonClicked(View view) {
        finish();
    }

    public void bookRoomButtonClicked(View view) {

     }
     //todo lav en slags dialog box, hvor man indtaster infomation til en booking


}
