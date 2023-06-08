package coop.constellation.connectorservices.tilesummary;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import coop.constellation.connectorservices.tilesummary.helpers.StdoutConnectorLogging;
import coop.constellation.connectorservices.tilesummary.helpers.EnhancedConnectorLogging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BeansConfig {

    // I am leaving this here so we remember not to do this.
    // Creating any ObjectMapper bean causes an error in springboot when deploying
    // to constellation portal, even with
    // a qualifier name.
    // @Bean(name="transfersMapper")
    // public ObjectMapper transfersMapper(){
    // ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    // return objectMapper;
    // }

    // You can use this bean by running the app with a Spring profile called "local"
    @Bean
    @Profile("local")
    ConnectorLogging localConnectorLogging(){
        return new StdoutConnectorLogging();
    }


    @Bean
    @Profile("!local")
    ConnectorLogging connectorLogging(){
        return new EnhancedConnectorLogging();
    }
}

