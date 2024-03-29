package coop.constellation.connectorservices.tilesummary.controller;

import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.tilesummary.handlers.TileSummaryHandler;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

// NOTE: Format for "@RequestMapping"
// RequestMapping("/externalConnector/[Connector Name]/[Connector Version Number]")

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/externalConnector/TileSummaryConnector/1.0")
public class TileSummaryController extends ConnectorControllerBase {

    private final TileSummaryHandler tileSummaryHandler;

    // Following method is required in order for your controller to pass health
    // checks.
    // If the server cannot call awsping and get the expected response yur app will
    // not be active.
    @GetMapping("/awsping")
    public String getAWSPing() {
        return "{ping: 'pong'}";
    }

    @PostMapping(path = "/tileSummary", consumes = "application/json", produces = "application/json")
    public ConnectorMessage getTileSummary(@RequestBody String connectorJson) {

        final String logPrefix = "TileSummaryController.tileSummary: ";
        final ConnectorMessage connectorMessage = handleConnectorMessage(logPrefix, connectorJson, tileSummaryHandler);
        return connectorMessage;
    }
}