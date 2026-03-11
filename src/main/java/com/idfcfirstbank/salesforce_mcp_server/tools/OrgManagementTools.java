package com.idfcfirstbank.salesforce_mcp_server.tools;

import com.idfcfirstbank.salesforce_mcp_server.cli.SalesforceCLIExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.idfcfirstbank.salesforce_mcp_server.utils.OrgManagementUtils;
import com.idfcfirstbank.salesforce_mcp_server.utils.CommonUtils;

@Service
public class OrgManagementTools {

    @Autowired
    private OrgManagementUtils orgManagementUtils;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private SalesforceCLIExecutor cliExecutor;

    private static final Logger log =
            LoggerFactory.getLogger(OrgManagementTools.class);


    @Tool(name = "list_orgs", description = """
            List all connected Salesforce orgs.
            Call this first if org alias is unknown.
            Show user the aliases and ask which org to use.
            """
    )
    public String listOrgs() {
        try {
            log.info("Listing orgs");
            return orgManagementUtils.cleanOrgListResponse(
                    cliExecutor.execute("sf", "org", "list", "--json")
            );
        } catch (Exception e) {
            log.error("list_orgs failed: {}", e.getMessage());
            return commonUtils.buildError("Failed to list orgs", e.getMessage());
        }
    }


    @Tool(name = "connect_to_org", description = """
            Connect to a Salesforce org via browser login.
            Ask user: 'production' or 'sandbox'?
            Ask user: what alias to give this org? (e.g. prod-org, my-sandbox)
            After login, call list_orgs to confirm connection.
            """
    )
    public String connectToOrg(
            @ToolParam(description = "Org type: 'production' or 'sandbox'")
            String orgType,
            @ToolParam(description = "Alias for this org. Lowercase, no spaces. Example: prod-org, my-sandbox")
            String alias) {

        if (alias == null || alias.isBlank())
            return commonUtils.buildError("Invalid alias", "Alias cannot be empty.");

        if (orgType == null || orgType.isBlank())
            return commonUtils.buildError("Invalid orgType", "Must be 'production' or 'sandbox'.");

        try {
            log.info("Connecting - type:{} alias:{}", orgType, alias);

            String raw = orgType.equalsIgnoreCase("sandbox")
                    ? cliExecutor.execute(
                    "sf", "org", "login", "web",
                    "--alias", alias,
                    "--instance-url", "https://test.salesforce.com",
                    "--json")
                    : cliExecutor.execute(
                    "sf", "org", "login", "web",
                    "--alias", alias,
                    "--json");

            return orgManagementUtils.cleanConnectResponse(raw, alias, orgType);
        } catch (Exception e) {
            log.error("connect_to_org failed: {}", e.getMessage());
            return commonUtils.buildError("Failed to connect", e.getMessage());
        }
    }


    @Tool(name = "check_org", description = """
            Check connection status and details of a Salesforce org.
            Use when user asks about org details or connection status.
            If error returned, ask user to run connect_to_org first.
            """
    )
    public String checkOrg(
            @ToolParam(description = "Org alias. Run list_orgs if unknown.")
            String alias) {

        if (alias == null || alias.isBlank())
            return commonUtils.buildError("Invalid alias", "Run list_orgs to see available orgs.");

        try {
            log.info("Checking org - alias:{}", alias);
            return orgManagementUtils.cleanCheckOrgResponse(
                    cliExecutor.execute(
                            "sf", "org", "display",
                            "--target-org", alias,
                            "--json")
            );
        } catch (Exception e) {
            log.error("check_org failed: {}", e.getMessage());
            return commonUtils.buildError("Failed to check org", e.getMessage());
        }
    }

    @Tool(name = "open_org", description = "Open a Salesforce org in the browser.")
    public String openOrg(@ToolParam(description = "Org alias. Run list_orgs if unknown.") String alias) {

        if (alias == null || alias.isBlank())
            return commonUtils.buildError("Invalid alias", "Run list_orgs to see available orgs.");

        try {
            log.info("Opening org - alias:{}", alias);
            cliExecutor.execute("sf", "org", "open", "--target-org", alias);
            return commonUtils.buildSuccess("Org " + alias + " opened in browser.");
        } catch (Exception e) {
            log.error("open_org failed: {}", e.getMessage());
            return commonUtils.buildError("Failed to open org", e.getMessage());
        }
    }

    @Tool(name = "disconnect_org", description = """
            Logout and disconnect from a Salesforce org.
            Always confirm with user before calling this.
            """
    )
    public String disconnectOrg(
            @ToolParam(description = "Org alias to disconnect. Run list_orgs if unknown.")
            String alias) {

        if (alias == null || alias.isBlank())
            return commonUtils.buildError("Invalid alias", "Run list_orgs to see available orgs.");

        try {
            log.info("Disconnecting - alias:{}", alias);
            cliExecutor.execute(
                    "sf", "org", "logout",
                    "--target-org", alias,
                    "--no-prompt",
                    "--json");
            return commonUtils.buildSuccess("Disconnected from org: " + alias);
        } catch (Exception e) {
            log.error("disconnect_org failed: {}", e.getMessage());
            return commonUtils.buildError("Failed to disconnect", e.getMessage());
        }
    }
}