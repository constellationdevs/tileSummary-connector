package coop.constellation.connectorservices.tilesummary.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Base class for implementing HandlerLogic
 */
@Component
@RequiredArgsConstructor
abstract class HandlerBase implements HandlerLogic {
    
    public static final ObjectMapper mapper = new ObjectMapper();
}