package net.devcats.stepit.Api;

import net.devcats.stepit.Api.Responses.CreateUserResponse;
import net.devcats.stepit.Api.Responses.GetCompetitionsResponse;
import net.devcats.stepit.Api.Responses.LoginResponse;
import net.devcats.stepit.Api.Responses.UpdateStepsResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StepItApi {

    @FormUrlEncoded
    @POST("api/?api=login")
    Call<LoginResponse> login(@Field("email") String email,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("api/?api=createUser")
    Call<CreateUserResponse> createUser(@Field("name") String name,
                                        @Field("email") String email,
                                        @Field("password") String password,
                                        @Field("gender") String gender,
                                        @Field("age") int age,
                                        @Field("deviceType") String deviceType,
                                        @Field("profilePicture") String profilePicture,
                                        @Field("accountType") int accountType);

    @GET("api/?api=getCompetitions")
    Call<GetCompetitionsResponse> getCompetitions(@Query("userId") int userId,
                                                  @Query("competitionId") int competitionId);

    @GET("api/?api=getCompetitions")
    Call<GetCompetitionsResponse> getCompetitions(@Query("userId") int userId);

    @FormUrlEncoded
    @POST("api/?api=updateSteps")
    Call<UpdateStepsResponse> updateSteps(@Field("userId") int userId,
                                          @Field("date") String date,
                                          @Field("stepCount") int stepCount);

}
