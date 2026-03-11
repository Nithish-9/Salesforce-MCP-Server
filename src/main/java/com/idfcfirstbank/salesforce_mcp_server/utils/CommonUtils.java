package com.idfcfirstbank.salesforce_mcp_server.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public String buildError(String error, String message) {
        try {
            ObjectNode r = mapper.createObjectNode();
            r.put("success", false);
            r.put("error",   error);
            r.put("message", message);
            return mapper.writeValueAsString(r);
        } catch (Exception e) {
            return "{\"success\":false,\"error\":\"" + error + "\"}";
        }
    }

    public String buildSuccess(String message) {
        try {
            ObjectNode r = mapper.createObjectNode();
            r.put("success", true);
            r.put("message", message);
            return mapper.writeValueAsString(r);
        } catch (Exception e) {
            return "{\"success\":true,\"message\":\"" + message + "\"}";
        }
    }

}
