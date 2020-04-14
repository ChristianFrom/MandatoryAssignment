package com.christianfrom.mandatoryassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.christianfrom.mandatoryassignment.Model.Room;
import com.christianfrom.mandatoryassignment.REST.ApiUtils;
import com.christianfrom.mandatoryassignment.REST.RoomRESTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PostLoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "TEST";
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView welcomeEditText = findViewById(R.id.welcomeTextView);
        welcomeEditText.setText("Welcome " + user.getEmail());
        getAllRooms();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuitems, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.myreservations) {
            Intent intent = new Intent(this, myBookings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void getAllRooms() {
        RoomRESTService rrs = ApiUtils.getRoomsService();
        Call<List<Room>> getAllRoomsCall = rrs.getAllRooms();
        TextView messageView = findViewById(R.id.mainMessageTextView);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        getAllRoomsCall.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful()) {
                    List<Room> allRooms = response.body();
                    Log.d(LOG_TAG, allRooms.toString());
                    progressBar.setVisibility(View.INVISIBLE);
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



    public void logoutFloatButtonPressed(View view) {
        FirebaseAuth.getInstance().signOut();
        refresh();
        Toast.makeText(this, "You have now logged out...", Toast.LENGTH_SHORT).show();
    }


    private void refresh() {
        Intent intent = new Intent(this, MainActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        //Todo Er der en bedre måde en det her?
    }
}
