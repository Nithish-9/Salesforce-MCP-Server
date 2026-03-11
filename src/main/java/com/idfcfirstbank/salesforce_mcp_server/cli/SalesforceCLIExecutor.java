package com.idfcfirstbank.salesforce_mcp_server.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SalesforceCLIExecutor {

    private static final Logger log = LoggerFactory.getLogger(SalesforceCLIExecutor.class);

    public String execute(String... commands) throws Exception {
        return execute(Arrays.asList(commands));
    }

    public String execute(List<String> commands) throws Exception {

        // Wrap with cmd /c for Windows
        List<String> fullCommand = new ArrayList<>();
        fullCommand.add("cmd");
        fullCommand.add("/c");
        fullCommand.addAll(commands);

        log.info("Running CLI: {}", String.join(" ", fullCommand));

        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        pb.redirectErrorStream(true);
        pb.environment().putAll(System.getenv()); // inherit PATH

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        log.info("CLI finished - exitCode: {}", exitCode);
        log.debug("CLI output: {}", output);

        return output.toString();
    }
}