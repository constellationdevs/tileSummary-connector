package coop.constellation.connectorservices.tilesummary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.ResponseStatusMessage;

import coop.constellation.connectorservices.tilesummary.service.TileSummaryHandler;
import coop.constellation.connectorservices.tilesummary.service.BasicTemplateImp;

import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import org.springframework.util.ResourceUtils;
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

        try {
            // CONVERT passed in JSON data into a ConnectorMessage
            connectorMessage = mapper.readValue(connectorJson, ConnectorMessage.class);

            logger.info(connectorMessage, "TileSummary-getLocalTextFile: BEGIN...");

            // BEGIN (YOUR LOGIC HERE) - FUNCTION TO GET LOCAL TEXT FILE
            // ==========================================================================================================

            String response = null;

            String filePath = "classpath:" + "tileSummaryResponse.json";
            File file = ResourceUtils.getFile(filePath);
            // Check make sure has value
            if (file.exists()) {
                // File exist read content
                String content = new String(Files.readAllBytes(file.toPath())).replace("\n", "");
                //.replace(" ", "");
                logger.info(connectorMessage, "CONTENT:  " + content);
                response = content;
            } else {
                // No file was found - return empty content
                response = "{}";
            }

            // ==========================================================================================================
            // END (YOUR LOGIC HERE) - FUNCTION TO GET LOCAL TEXT FILE
            // BEGIN - SET RESPONSE - Your Code here.
            // ==========================================================================================================
            connectorMessage.setResponse(response);

            logger.info(connectorMessage, "TileSummary-getLocalTextFile: Response - " + response);

            // ==========================================================================================================
            // END - SET RESPONSE
            // BEGIN - SET RESPONSE_STATUS
            // ==========================================================================================================
            responseStatusMessage.setStatus("OK");
            responseStatusMessage.setStatusCode("200");
            responseStatusMessage.setStatusDescription("Success");
            responseStatusMessage.setStatusReason("BasicTemplateController.getLocalTextFile - Has responded ");
            // ==========================================================================================================
            // END - SET RESPONSE_STATUS

        } catch (IOException ioE) {
            ioE.printStackTrace();
            CatchException(responseStatusMessage, connectorMessage, ioE.getMessage(),
                    "TileSummary failed: ");
        } catch (JsonSyntaxException jsE) {
            jsE.printStackTrace();
            CatchException(responseStatusMessage, connectorMessage, jsE.getMessage(),
                    "TileSummary failed: ");
        } catch (Exception e) {
            e.printStackTrace();
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