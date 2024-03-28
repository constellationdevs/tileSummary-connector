package coop.constellation.connectorservices.tilesummary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;
import com.xtensifi.dspco.*;

import coop.constellation.connectorservices.tilesummary.handlers.HandlerLogic;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class ConnectorControllerBase {

    @Getter
    private ObjectMapper mapper;

    @Autowired
    public void setObjectMapper(ObjectMapper om){
        this.mapper = om;
    }

    private ConnectorLogging clog;
    @Autowired public void setConnectorLogging(ConnectorLogging cl){
        this.clog = cl;
    }

    /**
     * Boilerplate method for handling the connector message
     * @param logPrefix A prefix for log messages and stats reasons
     * @param connectorJson The raw JSON for the request connector message
     * @param handlerLogic The custom logic for generating a response
     * @return a response connector message
     */
    ConnectorMessage handleConnectorMessage(final String logPrefix,
                                            final String connectorJson,
                                            final HandlerLogic handlerLogic) {
        ConnectorMessage connectorMessage = null;
        clog.info(connectorMessage, logPrefix + "BEGIN...");
        clog.info(connectorMessage, logPrefix + connectorJson);
        ResponseStatusMessage responseStatusMessage = null;
        try {

            connectorMessage = mapper.readValue(connectorJson, ConnectorMessage.class);
            UserData userData = connectorMessage.getExternalServicePayload().getUserData();
            final Map<String, String> allParams = getAllParams(connectorMessage);

            final String response = handlerLogic.generateResponse(allParams, userData, connectorMessage);

            connectorMessage.setResponse("{\"response\": " + response + "}");

            responseStatusMessage = new ResponseStatusMessage() {{
                setStatus("OK");
                setStatusCode("200");
                setStatusDescription("Success");
                setStatusReason(logPrefix + "Has responded.");
            }};
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
            if (connectorMessage == null) {
                clog.warn(connectorMessage, logPrefix + "Failed to create a connector message from the request, creating a new one for the response.");
                connectorMessage = new ConnectorMessage();
            }
            clog.info(connectorMessage, logPrefix + "END...");
            connectorMessage.setResponseStatus(responseStatusMessage);
        }
        return connectorMessage;
    }


    /**
     * Get all the value pairs out of the connector message.
     * NOTE: if a name occurs more than once, only the first occurrence is returned.
     * @param connectorMessage the request connector message
     * @return a Map of the value pairs
     */
    Map<String, String> getAllParams(final ConnectorMessage connectorMessage) {

        final Map<String, String> allParams = new HashMap<>();
        final ExternalServicePayload externalServicePayload = connectorMessage.getExternalServicePayload();
        final ConnectorParametersResponse connectorParametersResponse = connectorMessage.getConnectorParametersResponse();

        if (externalServicePayload != null) {
            final CustomData methodParams = externalServicePayload.getPayload();
            if(methodParams != null)
                for (ValuePair valuePair : methodParams.getValuePair()) {
                    allParams.putIfAbsent(valuePair.getName(), valuePair.getValue());
                }
        }
        if (connectorParametersResponse != null) {
            final CustomData otherParams = connectorParametersResponse.getParameters();
            if(otherParams != null) {
                for (ValuePair valuePair : otherParams.getValuePair()) {
                    allParams.putIfAbsent(valuePair.getName(), valuePair.getValue());
                }
            }
        }
        return allParams;
    }

    private void CatchException(ResponseStatusMessage responseStatusMessage, ConnectorMessage connectorMessage,
    String errorMsg, String Description) {
        clog.error(connectorMessage, Description + errorMsg);
        responseStatusMessage.setStatus("ERROR");
        responseStatusMessage.setStatusCode("500");
        responseStatusMessage.setStatusDescription("Failed");
        responseStatusMessage.setStatusReason(Description);
    }

}
