package com.example.projet_kotlin.activities

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projet_kotlin.R
import com.example.projet_kotlin.api.JsonFiles.readJsonFromFile
import com.example.projet_kotlin.api.JsonFiles.writeJsonToFile
import com.example.projet_kotlin.model.Country
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson




class CountryDetailsActivity : AppCompatActivity() {

    private lateinit var flagImageView: ImageView
    private lateinit var countryNameTextView: TextView
    private lateinit var capitalNameTextView: TextView
    private lateinit var populationTextView: TextView
    private lateinit var regionTextView: TextView
    private lateinit var subregionTextView: TextView
    private lateinit var areaTextView: TextView
    private lateinit var languageTextView: TextView
    private lateinit var addFavoriteButton : Button
    private lateinit var deleteFavoriteButton : Button
    private lateinit var backButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.country_details)

        flagImageView = findViewById(R.id.flagImageView)
        countryNameTextView = findViewById(R.id.countryNameTextView)
        capitalNameTextView = findViewById(R.id.capitalNameTextView)
        populationTextView = findViewById(R.id.populationTextView)
        regionTextView = findViewById(R.id.regionTextView)
        subregionTextView = findViewById(R.id.subregionTextView)
        areaTextView = findViewById(R.id.areaTextView)
        languageTextView = findViewById(R.id.languageTextView)
        addFavoriteButton = findViewById(R.id.addFavoriteButton)
        deleteFavoriteButton = findViewById(R.id.deleteFavoriteButton)
        backButton = findViewById(R.id.backButton)

        val countryName = intent.getStringExtra("country_name")
        if (countryName != null) {
            fetchCountryDetails(countryName)
        } else {
            Toast.makeText(this, "Country name is missing", Toast.LENGTH_SHORT).show()
        }

        addFavoriteButton.setOnClickListener {
            val countryName = countryNameTextView.text.toString()

            val jsonFileName = "countries.json"
            val jsonString = readJsonFromFile(this, jsonFileName)

            if (jsonString != null) {
                val gson = Gson()
                val type = object : TypeToken<List<Country>>() {}.type
                val countries = gson.fromJson<List<Country>>(jsonString, type)
                val country =
                    countries.firstOrNull { it.name.common.equals(countryName, ignoreCase = true) }

                if (country != null) {
                    val favoritesFileName = "favorite_countries.json"
                    val existingFavoritesJson = readJsonFromFile(this, favoritesFileName)
                    val existingFavorites: MutableList<Country> =
                        if (existingFavoritesJson != null) {
                            try {
                                gson.fromJson(
                                    existingFavoritesJson,
                                    object : TypeToken<MutableList<Country>>() {}.type
                                )
                            } catch (e: Exception) {
                                mutableListOf()
                            }
                        } else {
                            mutableListOf()
                        }

                    val isCountryAlreadyAdded = existingFavorites.any {
                        it.name.common.equals(
                            countryName,
                            ignoreCase = true
                        )
                    }
                    if (!isCountryAlreadyAdded) {
                        existingFavorites.add(country)

                        val updatedFavoritesJson = gson.toJson(existingFavorites)
                        writeJsonToFile(this, updatedFavoritesJson, favoritesFileName)

                        Toast.makeText(
                            this@CountryDetailsActivity,
                            "Country added to favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@CountryDetailsActivity,
                            "Country already exists in favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CountryDetailsActivity,
                        "Country not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@CountryDetailsActivity,
                    "Failed to retrieve data from local file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteFavoriteButton.setOnClickListener {

            val countryName = countryNameTextView.text.toString()
            val favoritesFileName = "favorite_countries.json"
            val existingFavoritesJson = readJsonFromFile(this, favoritesFileName)
            if (existingFavoritesJson != null) {
                val gson = Gson()
                val existingFavorites: MutableList<Country> = try {
                    gson.fromJson(existingFavoritesJson, object : TypeToken<MutableList<Country>>() {}.type)
                } catch (e: Exception) {
                    mutableListOf()
                }
                val countryExists = existingFavorites.any { it.name.common.equals(countryName, ignoreCase = true) }
                if (countryExists) {
                    val updatedFavorites = existingFavorites.filterNot { it.name.common.equals(countryName, ignoreCase = true) }
                    val updatedFavoritesJson = gson.toJson(updatedFavorites)
                    writeJsonToFile(this, updatedFavoritesJson, favoritesFileName)
                    Toast.makeText(this@CountryDetailsActivity, "Country removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CountryDetailsActivity, "Country not found in favorites", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@CountryDetailsActivity, "Failed to retrieve favorites data", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

        private fun fetchCountryDetails(name: String) {

        val jsonFileName = "countries.json"
        val jsonString = readJsonFromFile(this, jsonFileName)
        if (jsonString != null) {

            val gson = Gson()
            val type = object : TypeToken<List<Country>>() {}.type
            val countries = gson.fromJson<List<Country>>(jsonString, type)
            val country = countries.firstOrNull { it.name.common.equals(name, ignoreCase = true) }

            if (country != null) {
                displayCountryDetails(country)

            } else {
                Toast.makeText(this, "Country not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCountryDetails(country: Country) {

        Glide.with(this).load(country.flags.png).into(flagImageView)
        countryNameTextView.text = country.name.common
        capitalNameTextView.text = formatText("Capital : ${country.capital?.firstOrNull() ?: "No Capital"}")
        populationTextView.text = formatText("Population : ${country.population}")
        regionTextView.text = formatText("Region : ${country.region}")
        subregionTextView.text = formatText("Subregion : ${country.subregion}")
        areaTextView.text = formatText("Area : ${country.area} km²")

        val languages = country.languages
        if (languages != null) {
            languageTextView.text = formatText("Languages : ${languages.values.joinToString(", ")}")
        } else {
            languageTextView.text = formatText("Languages : N/A")
        }
    }

    private fun formatText(text: String): SpannableString {
        val spannableString = SpannableString(text)
        val colonIndex = text.indexOf(":")
        if (colonIndex != -1) {
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0, colonIndex + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableString
    }
}
