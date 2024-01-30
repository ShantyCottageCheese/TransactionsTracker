package tracker.transactionstracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    static {
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(requestFactory);
    }
}
