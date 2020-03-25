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
        getAllReservations();

    }





    private void getAllReservations() {
        ReservationRESTService rrs = ApiUtils.getReservationsService();
        Call<List<Reservation>> getAllReservationsCall = rrs.getAllReservations();
        TextView messageView = findViewById(R.id.mainMessageTextView);

        getAllReservationsCall.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    List<Reservation> allReservations = response.body();
                    Log.d(LOG_TAG, allReservations.toString());
                    populateRecyclerView(allReservations);
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
        RecyclerView recyclerView = findViewById(R.id.ReservationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(allReservations);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            Reservation reservation = (Reservation) item;
            Intent intent = new Intent(PostLoginActivity.this, SingleRoomActivity.class);
            intent.putExtra(SingleRoomActivity.ROOM, reservation);
            startActivity(intent);
        });
    }





    public void logoutFloatButtonPressed(View view) {
        mAuth.getInstance().signOut();
        finish();
        Toast.makeText(this, "You have now logged out...", Toast.LENGTH_SHORT).show();
    }
}
