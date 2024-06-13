package com.example.projet_kotlin.api

import com.example.projet_kotlin.model.Country
import retrofit2.Call
import retrofit2.http.GET

interface CountryApi {
    @GET("v3.1/all")
    fun getAllCountries(): Call<List<Country>>
}
