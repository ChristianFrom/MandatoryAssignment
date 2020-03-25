package com.christianfrom.mandatoryassignment.REST;

import com.christianfrom.mandatoryassignment.Model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ReservationRESTService {
    @GET("Reservations")
    Call<List<Reservation>> getAllReservations();
}
