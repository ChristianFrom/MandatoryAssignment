package com.christianfrom.mandatoryassignment.REST;

import com.christianfrom.mandatoryassignment.Model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReservationRESTService {
    @GET("Reservations")
    Call<List<Reservation>> getAllReservations();

    @GET("Reservations/room/{id}")
    Call<List<Reservation>> getRoomReservations(@Path("id")int id);

    @GET("Reservations/user/{userid}")
    Call<List<Reservation>> getRoomReservationsByUser(@Path("userid")String userid);

    @DELETE("Reservations/room/{id}")
    Call<Reservation> deleteReservation(@Path("id") int id);

    @POST("Reservations")
    Call<Reservation> saveReservationBody(@Body Reservation reservation);


}
