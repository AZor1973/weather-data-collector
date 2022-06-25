package ru.azor.wdc.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    @Value("${base-url}")
    private String baseUrl;
    @Value("${current-url}")
    private String currentUrl;
    @Value("${forecast-url}")
    private String forecastUrl;
    @Value("${timeout}")
    private int timeout;

    @Bean
    public WebClient webClientCurrentWeather() {
        return getWebClient(currentUrl);
    }

    @Bean
    public WebClient webClientForecastWeather() {
        return getWebClient(forecastUrl);
    }

    private WebClient getWebClient(String currentUrl) {
//        final TcpClient tcpClient = TcpClient
//                .create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
//                .doOnConnected(connection -> {
//                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
//                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
//                });

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl + currentUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}