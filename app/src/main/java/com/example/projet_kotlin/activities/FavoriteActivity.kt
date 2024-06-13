package com.example.projet_kotlin.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_kotlin.R
import com.example.projet_kotlin.api.JsonFiles
import com.example.projet_kotlin.model.Country
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.StringReader

class FavoriteActivity : AppCompatActivity() {

    private lateinit var adapter: CountryAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var countries: List<Country>
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)
        searchView = findViewById(R.id.searchView)

        setupSearchView()
        displayCountries()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun displayCountries() {

        val jsonFileName = "favorite_countries.json"
        val jsonString = JsonFiles.readJsonFromFile(this, jsonFileName)

        if (jsonString != null) {
            try {
                val gson = Gson()
                val reader = JsonReader(StringReader(jsonString))
                reader.isLenient = true
                val type = object : TypeToken<List<Country>>() {}.type
                countries = gson.fromJson(reader, type)
                adapter = CountryAdapter(countries)
                recyclerView.adapter = adapter
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
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
}
