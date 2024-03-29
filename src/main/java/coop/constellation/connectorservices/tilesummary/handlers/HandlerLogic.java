package coop.constellation.connectorservices.tilesummary.handlers;

import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * Interface for the custom logic to generate a response
 */
@FunctionalInterface
public interface HandlerLogic {
    String generateResponse(final Map<String, String> parms, UserData userData, ConnectorMessage cm) throws IOException, ParseException;
}
