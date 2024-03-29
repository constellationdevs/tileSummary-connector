package coop.constellation.connectorservices.tilesummary.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import coop.constellation.connectorservices.tilesummary.models.TileSummary;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TileSummaryHandler extends HandlerBase {
    private final ConnectorLogging clog;

    @Override
    public String generateResponse(Map<String, String> parms, UserData userData, ConnectorMessage cm)
            throws IOException, JsonProcessingException, ParseException, DatabindException {

        String response = "";
        try {
            
            clog.info(cm, "TileSummary-getLocalTextFile: BEGIN...");
            String templateId = parms.getOrDefault("templateId", "1");
            String filePath = "/tileSummaryResponse" + templateId + ".json";

            InputStream fileStream = this.getClass().getResourceAsStream(filePath);
            TileSummary ts = mapper.readValue(fileStream, TileSummary.class);
            
            response = mapper.writeValueAsString(ts);

            clog.info(cm, "TileSummary-getLocalTextFile: Response - " + response);


        } catch (IOException ioE) {
            clog.error(cm, ioE.getMessage());
            throw ioE;
        } 
        
        clog.info(cm, "TileSummary-getLocalTextFile: END...");

        return response;
    }
    
}
