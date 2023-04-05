package com.example.walking


import com.example.walking.model.*

import retrofit2.Call
import retrofit2.http.*

interface INetworkService {
    @POST("walking/user/join")
    fun doInsertUser(@Body user: User?): Call<User>

    @POST("login")
    fun login(@Body loginDto: LoginDto): Call<LoginDto>

    @GET("walking/user/oneUser")
    fun doGetOneUser(@Query("email") email: String?): Call<User>

    @GET("walking/user/oneUserByNick")
    fun doGetOneUserByNickname(@Query("nickname") nickname: String?): Call<User>

    @POST("walking/user/update")
    fun doUpdateUser(@Body user: User?): Call<User>

    @GET("walking/user/delete")
    fun doDeleteUser(@Query("email") email:String?): Call<Int>



    //meeting
    @GET("walking/meeting/list")
    fun doGetMeetingList(): Call<MeetingListModel>

    @GET("walking/meeting/oneMeeting")
    fun doGetOneMeeting(@Query("title") title:String?): Call<Meeting>

    @POST("walking/meeting/insert")
    fun doInsertMeeting(@Body meeting: Meeting?): Call<Meeting>

    @POST("walking/meeting/update")
    fun doUpdateMeeting(@Body meeting: Meeting?): Call<Meeting>

    @POST("walking/meeting/insertuserinmeeting")
    fun doInsertUserinmeeting(@Body userinmeeting: Userinmeeting?): Call<Userinmeeting>

    @GET("walking/meeting/oneUserinmeeting")
    fun doGetOneUserinmeeting(@Query("userinmeeting_val") userinmeeting_val:String?): Call<Userinmeeting>

    @GET("walking/meeting/chatMemberList")
    fun doGetChatMemberList(@Query("meeting_id") meeting_id:Int?): Call<List<User>>

    @GET("walking/meeting/out")
    fun doDeleteUserinmeeting(@Query("email") email:String?, @Query("meeting_id") meeting_id:Int?): Call<Int>
    @GET("walking/meeting/delete")
    fun doDeleteMeeting(@Query("meeting_id") meeting_id:Int?): Call<Int>

    @POST("walking/board/insert")
    // @post (~~~) 부분의 url 로 보드 객체를 가지고 서버로(현재는 스프링) 접속한다.
    fun doInsertBoard(@Body board: Board?): Call<Board>

    @GET("walking/board/list")
    fun doGetBoardList(): Call<BoardListModel>
}