package ml.espgm.gesem.configurations

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
            JsonMapper.builder()
                    .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                    .findAndAddModules()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build()
}