package kush.example.contactsdemo.api

import kush.example.contactsdemo.model.CommonModel
import kush.example.contactsdemo.model.ContactModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @Multipart
    @POST("addContact.php")
    fun addContact(
        @Part("image\"; filename=\"profile.jpg\" ") image: RequestBody?,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("phone_no") phone_no: RequestBody,
        @Part("email") email: RequestBody,
    ): Call<CommonModel>

    @POST("getContacts.php")
    fun getContacts(): Call<ContactModel>

    @FormUrlEncoded
    @POST("deleteContact.php")
    fun deleteContact(
        @Field("id") id: String,
    ): Call<CommonModel>

    @Multipart
    @POST("updateContact.php")
    fun updateContact(
        @Part("id") id: RequestBody,
        @Part("image\"; filename=\"profile.jpg\" ") image: RequestBody?,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("phone_no") phone_no: RequestBody,
        @Part("email") email: RequestBody,
    ): Call<CommonModel>

}


