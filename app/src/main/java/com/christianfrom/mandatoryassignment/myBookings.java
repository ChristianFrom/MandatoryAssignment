package com.christianfrom.mandatoryassignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
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
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final ReservationRESTService rrs = ApiUtils.getReservationsService();
    private GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        gestureDetector = new GestureDetectorCompat(this, new myBookings.GestureListener());
        getMyReservations();
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean leftSwipe = e1.getX() < e2.getX();
            if (leftSwipe){
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    private void getMyReservations() {

        Call<List<Reservation>> getMyReservationsCall = rrs.getRoomReservationsByUser(user.getUid());
        TextView errorText = findViewById(R.id.errorTextView);
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


    private void populateRecyclerView(List<Reservation> myReservations) {
        RecyclerView recyclerView = findViewById(R.id.myReservationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(myReservations);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position, item) -> {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(myBookings.this);
            View mView = getLayoutInflater().inflate(R.layout.single_booking_dialog, null);
            Button deleteButton = mView.findViewById(R.id.deleteButton);
            dialog.setView(mView);
            final AlertDialog alertDialog = dialog.create();
            alertDialog.setCanceledOnTouchOutside(true);

            Reservation reservationToDelete = (Reservation) item;
            int reservationId = reservationToDelete.getId();

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("delete", "delete button pressed");
                   Call<Void> deleteReservationCall = rrs.deleteReservation(reservationId);
                   deleteReservationCall.enqueue(new Callback<Void>() {
                       @Override
                       public void onResponse(Call<Void> call, Response<Void> response) {
                           if (response.isSuccessful()){
                               Toast.makeText(myBookings.this, "Booking: " + reservationId + " has been deleted", Toast.LENGTH_SHORT).show();
                               alertDialog.dismiss();
                               refresh();
                           }
                           else {
                               Toast.makeText(myBookings.this, "Booking: " + reservationId + " could not be deleted", Toast.LENGTH_SHORT).show();
                           }
                       }

                       @Override
                       public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("deleteerror", "Problem: " + t.getMessage());
                       }
                   });
                }
            });
            alertDialog.show();

        });
    }


    private void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        //Todo Er der en bedre måde en det her?
    }
}



