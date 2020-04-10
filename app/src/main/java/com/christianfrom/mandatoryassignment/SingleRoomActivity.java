package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.christianfrom.mandatoryassignment.Model.Reservation;
import com.christianfrom.mandatoryassignment.Model.Room;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.ReservationRESTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;
import java.util.List;
import static java.lang.Math.toIntExact;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Todo Tilføj swipe gesture, så man kan swipe tilbage.

public class SingleRoomActivity extends AppCompatActivity {
    private int mYear, mMonth, mDay, fromHour, fromMinute, toHour, toMinute;
    public static final String ROOM = "room";
    private static final String LOG_TAG = "ROOMS";
    private Room room;
    private FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    int timeFromSeconds;
    int timeToSeconds;

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

        if (room.getRemarks() == null) {
            remarks.setText("No remarks for this room.");
        } else {
            remarks.setText(room.getRemarks());
        }
    }


    public void bookRoomButtonClicked(View view) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SingleRoomActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.booking_dialog, null);
        final EditText purposeText = mView.findViewById(R.id.purposeText);
        TextView txtDate = mView.findViewById(R.id.dateChosen);
        TextView txtTimeFrom = mView.findViewById(R.id.timeFromChosen);
        TextView txtTimeTo = mView.findViewById(R.id.timeToChosen);
        Button datetime = mView.findViewById(R.id.dateButton);
        Button timeFrom = mView.findViewById(R.id.fromTimeButton);
        Button timeTo = mView.findViewById(R.id.toTimeButton);
        Button btn_book = mView.findViewById(R.id.bookButton);
        dialog.setView(mView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(true);

        Calendar combinedCalFrom = Calendar.getInstance();
        Calendar combinedCalTo = Calendar.getInstance();

        datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SingleRoomActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                txtDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                combinedCalFrom.set(year, month, dayOfMonth);
                                combinedCalTo.set(year, month, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                fromHour = c.get(Calendar.HOUR_OF_DAY);
                fromMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SingleRoomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String format = "%1$02d";
                        txtTimeFrom.setText(String.format(format, hourOfDay) + ":" + String.format(format, minute));
                        combinedCalFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        combinedCalFrom.set(Calendar.MINUTE, minute);

                        long tid = combinedCalFrom.getTimeInMillis() / 1000;
                        timeFromSeconds = toIntExact(tid);
                    }
                }, fromHour, fromMinute, true);
                timePickerDialog.show();

            }
        });

        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                toHour = c.get(Calendar.HOUR_OF_DAY);
                toMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SingleRoomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        combinedCalTo.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        combinedCalTo.set(Calendar.MINUTE, minute);
                        Log.d("date", combinedCalTo.toString());

                        long tid = combinedCalTo.getTimeInMillis() / 1000;

                        if (combinedCalFrom.getTimeInMillis() >= combinedCalTo.getTimeInMillis()) {
                            Toast.makeText(SingleRoomActivity.this, "'TimeFrom' can't be higher or equal 'TimeTo'", Toast.LENGTH_SHORT).show();
                            combinedCalTo.set(Calendar.HOUR_OF_DAY, Calendar.MINUTE);
                        } else {
                            String format = "%1$02d";
                            txtTimeTo.setText(String.format(format, hourOfDay) + ":" + String.format(format, minute));
                            timeToSeconds = toIntExact(tid);
                        }

                    }
                }, toHour, toMinute, true);
                timePickerDialog.show();


            }
        });

        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = user.getUid();
                String purpose = purposeText.getText().toString().trim();
                int roomId = room.getId();

                ReservationRESTService rrs = ApiUtils.getReservationsService();
                Reservation reservation = new Reservation(timeFromSeconds, timeToSeconds, userId, purpose, roomId);

                Call<Reservation> saveReservationCall = rrs.saveReservationBody(reservation);
                saveReservationCall.enqueue(new Callback<Reservation>() {
                    @Override
                    public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                        Reservation newReservation = response.body();
                        Log.d("newreservation", newReservation.toString());
                        if (true)  {
                            //Reservation newReservation = response.body();
                            //Log.d("newreservation", newReservation.toString());

                            Toast.makeText(SingleRoomActivity.this, "New reservation added, id: " + newReservation.getId(), Toast.LENGTH_SHORT);
                            //Todo få toasten til at komme frem, og lukke dialog boksen, når den er færdig.
                        } else {
                            String problem = "Problem: " + response.code() + " " + response.message();
                            Log.e("reservation", problem);
                            Toast.makeText(SingleRoomActivity.this, "problem", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("post", response.toString());
                    }

                    @Override
                    public void onFailure(Call<Reservation> call, Throwable t) {
                        Log.e("reservation", t.getMessage());
                    }
                });
            }
        });
        alertDialog.show();
    }


    public void userLoggedIn() {
        if (user != null) {
            Log.d("user", user.getEmail());
            Button bookRoomButton = findViewById(R.id.bookRoomButton);
            bookRoomButton.setEnabled(true);
        } else {
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
                    } else {
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

    private void populateRecyclerView(List<Reservation> allReservations) {
        RecyclerView recyclerView = findViewById(R.id.singleRoomRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewSimpleAdapter adapter = new RecyclerViewSimpleAdapter<>(allReservations);
        recyclerView.setAdapter(adapter);
    }

    public void backButtonClicked(View view) {
        finish();
    }
}
