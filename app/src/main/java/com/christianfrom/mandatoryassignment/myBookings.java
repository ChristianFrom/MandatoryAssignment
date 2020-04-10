package com.christianfrom.mandatoryassignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.christianfrom.mandatoryassignment.Model.Reservation;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.ReservationRESTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class myBookings extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        getMyReservations();
    }


    private void getMyReservations() {

        TextView errorText = findViewById(R.id.errorTextView);
        ReservationRESTService rrs = ApiUtils.getReservationsService();
        Call<List<Reservation>> getMyReservationsCall = rrs.getRoomReservationsByUser(user.getUid());
        getMyReservationsCall.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    List<Reservation> myReservations = response.body();
                    if (myReservations.toString() == "[]") {
                        errorText.setVisibility(View.VISIBLE);
                    } else {
                        populateRecyclerView(myReservations);
                        errorText.setVisibility(View.INVISIBLE);
                    }
                } else {
                    String message = "Problem " + response.code() + " " + response.message();
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }




    private void populateRecyclerView(List<Reservation> myReservations)
    {
        RecyclerView recyclerView = findViewById(R.id.myReservationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(myReservations);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
                //Todo Tilføj dialog som bruges til at slette reservationen
        });
    }

}





