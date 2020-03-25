package com.christianfrom.mandatoryassignment.REST;

public class ApiUtils {

    public ApiUtils() {
    }

    private static final String BASE_URL = "http://anbo-roomreservationv3.azurewebsites.net/api/";
    public static RoomRESTService getRoomsService(){

        return RetrofitClient.getClient(BASE_URL).create(RoomRESTService.class);
    }
    public static ReservationRESTService getReservationsService(){
        return RetrofitClient.getClient(BASE_URL).create(ReservationRESTService.class);
    }
}
