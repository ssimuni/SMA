package com.example.simu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simu.CountriesQuery;
import com.example.simu.R;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<CountriesQuery.Country> countries;
    private Context context;

    public CountryAdapter(Context context, List<CountriesQuery.Country> countries) {
        this.context = context;
        this.countries = countries;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        CountriesQuery.Country country = countries.get(position);
        holder.countryNameTextView.setText(getOrDefault(country.name(), "Not defined"));
        holder.countryCapitalTextView.setText(getOrDefault(country.capital(), "Not defined"));
        holder.countryEmojiTextView.setText(getOrDefault(country.emoji(), "Not defined"));
        holder.countryCode.setText(getOrDefault(country.code(), "Not defined"));
        holder.countryCurrency.setText(getOrDefault(country.currency(), "Not defined"));
        holder.countryLanguages.setText(getLanguagesString(country.languages()));
        holder.countryContinent.setText(getOrDefault(country.continent().name(), "Not defined"));
    }


    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private String getLanguagesString(List<CountriesQuery.Language> languages) {
        StringBuilder languagesStr = new StringBuilder();
        for (CountriesQuery.Language language : languages) {
            languagesStr.append(language.name()).append(", ");
        }
        return languagesStr.length() > 0 ? languagesStr.substring(0, languagesStr.length() - 2) : "Not Defined";
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView countryEmojiTextView;
        TextView countryNameTextView;
        TextView countryCapitalTextView,countryCode,countryCurrency,countryLanguages,countryContinent;


        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            countryEmojiTextView = itemView.findViewById(R.id.countryEmojiTextView);
            countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
            countryCapitalTextView = itemView.findViewById(R.id.countryCapitalTextView);
            countryCode= itemView.findViewById(R.id.countryCode);
            countryCurrency=itemView.findViewById(R.id.countryCurrency);
            countryLanguages=itemView.findViewById(R.id.countryLanguages);
            countryContinent=itemView.findViewById(R.id.countryContinent);
        }
    }
}