package coop.constellation.connectorservices.tilesummary.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConnectorResponse {
    private Object response;
    private Boolean success;
    private String message;
}
