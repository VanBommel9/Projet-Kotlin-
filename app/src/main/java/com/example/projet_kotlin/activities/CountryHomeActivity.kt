package com.example.projet_kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_kotlin.api.RetrofitInstance
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.projet_kotlin.R
import com.example.projet_kotlin.api.JsonFiles.writeJsonToFile
import com.example.projet_kotlin.model.Country


class CountryHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryAdapter
    private lateinit var searchView: SearchView
    private lateinit var favoriteButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        favoriteButton = findViewById(R.id.favoriteButton)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCountries()
        setupSearchView()

        favoriteButton.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (!query.isNullOrEmpty()) {
                    adapter.filter(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    adapter.filter(newText)
                } else {
                    adapter.filter("")
                }
                return true
            }
        })
    }


    private fun fetchCountries() {

        val jsonFileName = "countries.json"
        RetrofitInstance.api.getAllCountries().enqueue(object : Callback<List<Country>> {

            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful && response.body() != null) {
                    val countries = response.body()!!

                    val gson = Gson()
                    val json = gson.toJson(countries)
                    writeJsonToFile(this@CountryHomeActivity, json, jsonFileName)

                    adapter = CountryAdapter(countries)
                    recyclerView.adapter = adapter

                } else {
                    Toast.makeText(this@CountryHomeActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Toast.makeText(this@CountryHomeActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}

