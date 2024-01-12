package com.ssdam.party.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {
    @Value("${owmApiKey}")
    private String apiKey;
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<String> getWeather(String latitude, String longitude, LocalDateTime dateTime) {
        // OpenWeatherMap API 호출을 위한 URL 생성
        String apiUrl = String.format("%s?lat=%s&lon=%s&appid=%s",API_URL, latitude, longitude, apiKey);
        // RestTemplate을 사용하여 API 호출
        WeatherResult response = restTemplate.getForObject(apiUrl, WeatherResult.class);
        //입력받은 dateTime을 문자열로 변환
        String formattedDateTime = getFormattedDateTime(dateTime);

        try {
            List<WeatherInfo.Weather> weather = response.getResults()
                    .stream()
                    .filter(weatherInfo -> weatherInfo.getDateTimeText().startsWith(formattedDateTime))
                    .findFirst()
                    .map(weatherInfo1 -> weatherInfo1.getWeather())
                    .orElse(null);
            String description = weather.stream()
                    .findFirst()
                    .map(weather1 -> weather1.getDescription())
                    .orElse(null);

            return Optional.ofNullable(description);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String getFormattedDateTime(LocalDateTime dateTime) {
        int dateTimeHour = dateTime.getHour()/3;//현재시간을 3으로 나눈 몫 가져오기
        String formattedHour = String.format("%02d", dateTimeHour*3);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd "+formattedHour));
    }
}

