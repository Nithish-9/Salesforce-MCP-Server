package com.idfcfirstbank.salesforce_mcp_server.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.idfcfirstbank.salesforce_mcp_server.cli.SalesforceCLIExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataOperationTools {

    private static final Logger log = LoggerFactory.getLogger(OrgManagementTools.class);

    @Autowired
    private SalesforceCLIExecutor cliExecutor;

}
