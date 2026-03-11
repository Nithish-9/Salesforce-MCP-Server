package com.idfcfirstbank.salesforce_mcp_server.utils;

import com.idfcfirstbank.salesforce_mcp_server.tools.OrgManagementTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class OrgManagementUtils {

    private static final Logger log =
            LoggerFactory.getLogger(OrgManagementUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public String cleanOrgListResponse(String raw) {
        log.info(raw);
        try {
            JsonNode root = mapper.readTree(raw);
            ArrayNode orgs = mapper.createArrayNode();

            for (JsonNode org : root.path("result").path("nonScratchOrgs")) {
                ObjectNode o = mapper.createObjectNode();
                o.put("alias",       org.path("alias").asText());
                o.put("username",    org.path("username").asText());
                o.put("instanceUrl", org.path("instanceUrl").asText());
                o.put("isDefault",   org.path("isDefaultUsername").asBoolean());
                o.put("isSandbox",   org.path("isSandbox").asBoolean());
                o.put("status",      org.path("connectedStatus").asText());
                orgs.add(o);
            }

            for (JsonNode org : root.path("result").path("scratchOrgs")) {
                ObjectNode o = mapper.createObjectNode();
                o.put("alias",      org.path("alias").asText());
                o.put("username",   org.path("username").asText());
                o.put("expiryDate", org.path("expirationDate").asText());
                o.put("isScratch",  true);
                orgs.add(o);
            }

            ObjectNode result = mapper.createObjectNode();
            result.put("totalOrgs", orgs.size());
            result.set("orgs", orgs);

            return mapper.writeValueAsString(result);

        } catch (Exception e) {
            return raw;
        }
    }

    public String cleanConnectResponse(String raw, String alias, String orgType) {
        log.info(raw);
        try {
            JsonNode root = mapper.readTree(raw);
            ObjectNode result = mapper.createObjectNode();
            result.put("success",  true);
            result.put("alias",    alias);
            result.put("orgType",  orgType);
            result.put("username", root.path("result").path("username").asText());
            result.put("message",  "Connected to " + orgType + " org: " + alias);
            return mapper.writeValueAsString(result);
        } catch (Exception e) {
            return raw;
        }
    }

    public String cleanCheckOrgResponse(String raw) {
        log.info(raw);
        try {
            JsonNode result = mapper.readTree(raw).path("result");

            ObjectNode cleaned = mapper.createObjectNode();
            cleaned.put("alias",       result.path("alias").asText());
            cleaned.put("username",    result.path("username").asText());
            cleaned.put("instanceUrl", result.path("instanceUrl").asText());
            cleaned.put("orgId",       result.path("id").asText());
            cleaned.put("isSandbox",   result.path("isSandbox").asBoolean());
            cleaned.put("isConnected",
                    !result.path("accessToken").asText().isEmpty());

            return mapper.writeValueAsString(cleaned);

        } catch (Exception e) {
            return raw;
        }
    }
}
