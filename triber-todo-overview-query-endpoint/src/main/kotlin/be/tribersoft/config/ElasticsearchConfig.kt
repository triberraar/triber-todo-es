package be.tribersoft.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.client.Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.DefaultResultMapper
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.EntityMapper
import org.springframework.stereotype.Component

@Configuration
open class ElasticsearchConfig(val elasticsearchEntityMapper: ElasticsearchEntityMapper) {

    @Bean
    open fun elasticsearchTemplate(client: Client) = ElasticsearchTemplate(client, DefaultResultMapper(elasticsearchEntityMapper))

}

@Component
class ElasticsearchEntityMapper(val objectMapper: ObjectMapper) : EntityMapper {
    override fun mapToString(obj: Any?) = objectMapper.writeValueAsString(obj)

    override fun <T : Any?> mapToObject(source: String?, clazz: Class<T>?) = objectMapper.readValue(source, clazz)

}