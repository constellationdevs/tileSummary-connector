package coop.constellation.connectorservices.tilesummary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.ResponseStatusMessage;

import coop.constellation.connectorservices.tilesummary.models.ConnectorResponse;
import coop.constellation.connectorservices.tilesummary.models.TileSummary;

import java.io.IOException;
import java.io.InputStream;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import org.springframework.web.bind.annotation.*;

// NOTE: Format for "@RequestMapping"
// RequestMapping("/externalConnector/[Connector Name]/[Connector Version Number]")

@RestController
@RequestMapping("/externalConnector/TileSummaryConnector/1.0")
public class TileSummaryController {

    // Following method is required in order for your controller to pass health
    // checks.
    // If the server cannot call awsping and get the expected response yur app will
    // not be active.
    @GetMapping("/awsping")
    public String getAWSPing() {
        return "{ping: 'pong'}";
    }

    // Logger for this object
    private ConnectorLogging logger = new ConnectorLogging();

    @CrossOrigin
    @PostMapping(path = "/tileSummary", consumes = "application/json", produces = "application/json")
    public ConnectorMessage getTileSummary(@RequestBody String connectorJson) {

        ConnectorMessage connectorMessage = null;
        ResponseStatusMessage responseStatusMessage = new ResponseStatusMessage();
        ObjectMapper mapper = new ObjectMapper();
        ConnectorResponse response = new ConnectorResponse();

        try {
            connectorMessage = mapper.readValue(connectorJson, ConnectorMessage.class);

            logger.info(connectorMessage, "TileSummary-getLocalTextFile: BEGIN...");

            String filePath = "/tileSummaryResponse.json";
            InputStream fileStream = this.getClass().getResourceAsStream(filePath);
            TileSummary ts = mapper.readValue(fileStream, TileSummary.class);
            
            response.setResponse(ts);
            response.setMessage("tileSummary responded successfully.");
            response.setSuccess(true);

            connectorMessage.setResponse(mapper.writeValueAsString(response));

            logger.info(connectorMessage, "TileSummary-getLocalTextFile: Response - " + response);

            responseStatusMessage.setStatus("OK");
            responseStatusMessage.setStatusCode("200");
            responseStatusMessage.setStatusDescription("Success");
            responseStatusMessage.setStatusReason("TileSummary.getLocalTextFile - Has responded ");
            // ==========================================================================================================
            // END - SET RESPONSE_STATUS

        } catch (NullPointerException npE) {
            CatchException(responseStatusMessage, connectorMessage, npE.getMessage(),
                    "TileSummary failed to find resource file: ");
        } catch (IOException ioE) {
            CatchException(responseStatusMessage, connectorMessage, ioE.getMessage(),
                    "TileSummary failed: ");
        } catch (JsonSyntaxException jsE) {
            CatchException(responseStatusMessage, connectorMessage, jsE.getMessage(),
                    "TileSummary failed: ");
        } catch (Exception e) {
            CatchException(responseStatusMessage, connectorMessage, e.getMessage(),
                    "TileSummary failed: ");
        } finally {
            connectorMessage.setResponseStatus(responseStatusMessage);
        }

        logger.info(connectorMessage, "TileSummary-getLocalTextFile: END...");
        return connectorMessage;
    }

    private void CatchException(ResponseStatusMessage responseStatusMessage, ConnectorMessage connectorMessage,
            String errorMsg, String Description) {
        logger.error(connectorMessage, Description + errorMsg);
        responseStatusMessage.setStatus("ERROR");
        responseStatusMessage.setStatusCode("500");
        responseStatusMessage.setStatusDescription("Failed");
        responseStatusMessage.setStatusReason(Description);
    }
}