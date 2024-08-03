package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.simu.Models.WeatherData;
import com.example.simu.Models.main;
import com.example.simu.Models.weather;
import com.example.simu.databinding.ActivityWeatherBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {
ActivityWeatherBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        String currentdate = format.format(new Date());

        binding.date.setText(currentdate);

        fetchWeather("Chittagong");
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((TextUtils.isEmpty(binding.searchBar.getText().toString()))){
                    binding.searchBar.setError("Please enter city");
                    return;
                }

                String City_Name = binding.searchBar.getText().toString();
                fetchWeather(City_Name);
            }
        });


    }

    void fetchWeather(String cityname){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

        Call<WeatherData> call = interfaceApi.getData(cityname, "dc42d705bc5c8f87617f6966cffd5697", "metric");

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if(response.isSuccessful()){
                    WeatherData weatherData = response.body();
                    Log.d("Weather", "Weather data: " + weatherData);
                    assert weatherData != null;
                    main to = weatherData.getMain();

                    binding.celcius.setText(String.valueOf(to.getTemp())+"\u2103");
                    binding.maxTemp.setText(String.valueOf(to.getFeels_like())+"\u2103");
                    binding.minTemp.setText(String.valueOf(to.getTemp_min())+"\u2103");
                    binding.pressure.setText(String.valueOf(to.getPressure()));
                    binding.humidity.setText(String.valueOf(to.getHumidity()));
                    binding.city.setText(weatherData.getName());

                    List<weather> description = weatherData.getWeather();

                    for (weather data:description){
                        binding.description.setText(data.getDescription());
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("Weather", "Failed to fetch weather data: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}