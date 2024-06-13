package com.example.projet_kotlin.activities

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projet_kotlin.R
import com.example.projet_kotlin.model.Country

class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val countryNameTextView: TextView = view.findViewById(R.id.countryNameTextView)
    val capitalNameTextView: TextView = view.findViewById(R.id.capitalNameTextView)
    val flagImageView: ImageView = view.findViewById(R.id.flagImageView)
    val cardView: CardView = view.findViewById(R.id.country_view_cardview)
}

class CountryAdapter(private var countries: List<Country>) : RecyclerView.Adapter<CountryViewHolder>() {

    private var countriesFiltered: List<Country> = countries.sortedBy { it.name.common }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countriesFiltered[position]
        holder.countryNameTextView.text = country.name.common
        holder.capitalNameTextView.text = country.capital?.firstOrNull() ?: "No Capital"
        Glide.with(holder.itemView.context).load(country.flags.png).into(holder.flagImageView)

        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CountryDetailsActivity::class.java)
            intent.putExtra("country_name", country.name.common)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = countriesFiltered.size

    fun filter(query: String) {
        countriesFiltered = if (query.isEmpty()) {
            countries.sortedBy { it.name.common }
        } else {
            countries.map { country ->
                val relevanceScore = calculateRelevanceScore(country, query)
                Pair(country, relevanceScore)
            }
                .filter { (_, score) -> score > 0 }
                .sortedByDescending { (_, score) -> score }
                .map { (country, _) -> country }
        }
        notifyDataSetChanged()
    }

    private fun calculateRelevanceScore(country: Country, query: String): Int {
        val nameScore = if (country.name.common.contains(query, ignoreCase = true)) 2 else 0
        val capitalScore = if (country.capital?.isNotEmpty() == true && country.capital.first().contains(query, ignoreCase = true)) 1 else 0
        return nameScore + capitalScore
    }
}
