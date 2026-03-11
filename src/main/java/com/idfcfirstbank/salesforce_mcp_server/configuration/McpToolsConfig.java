package com.idfcfirstbank.salesforce_mcp_server.configuration;

import com.idfcfirstbank.salesforce_mcp_server.tools.OrgManagementTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class McpToolsConfig {

    @Bean
    public List<ToolCallback> tools(OrgManagementTools orgManagementTools) {
        return List.of(ToolCallbacks.from(orgManagementTools));
    }

}
