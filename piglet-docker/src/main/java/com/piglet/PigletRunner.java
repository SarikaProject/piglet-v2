package com.piglet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class PigletRunner {

    private static final String OUTPUT_DIR = "/piglet/output";
    private static final String RESOURCES_DIR = "/piglet/resources";

    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          PigletV2 - QA Testing Framework                        ║");
        System.out.println("║                  Running Example Tests                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create output directory if it doesn't exist
        new File(OUTPUT_DIR).mkdirs();

        long startTime = System.currentTimeMillis();

        // Initialize test results
        TestResults testResults = new TestResults();

        try {
            // Step 1: Run BeanShell Scripts
            System.out.println("Step 1: Executing BeanShell Scripts for QA Testing");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            BeanShellScriptRunner beanShellRunner = new BeanShellScriptRunner(RESOURCES_DIR);
            testResults.addAll(beanShellRunner.executeAllScripts());
            System.out.println();

            // Step 2: Execute SPARQL Queries
            System.out.println("Step 2: Executing SPARQL Queries");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            SparqlQueryExecutor sparqlExecutor = new SparqlQueryExecutor(RESOURCES_DIR);
            testResults.addAll(sparqlExecutor.executeAllQueries());
            System.out.println();

            // Step 3: Generate HTML Report
            System.out.println("Step 3: Generating HTML Test Report");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            generateHTMLReport(testResults, executionTime);
            System.out.println();

            // Summary
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                      TEST EXECUTION SUMMARY                    ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println(String.format("║ Total Tests:      %-48d ║", testResults.getTotalTests()));
            System.out.println(String.format("║ Passed:           %-48d ║", testResults.getPassedTests()));
            System.out.println(String.format("║ Failed:           %-48d ║", testResults.getFailedTests()));
            System.out.println(String.format("║ Execution Time:   %-48s ║", 
                String.format("%d ms", executionTime)));
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println(String.format("║ HTML Report Generated: %s ║", OUTPUT_DIR + "/piglet-test-report.html"));
            System.out.println("╚════════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateHTMLReport(TestResults results, long executionTime) throws IOException {
        StringBuilder html = new StringBuilder();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>PigletV2 - QA Test Report</title>\n");
        html.append("    <style>\n");
        html.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }\n");
        html.append("        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); overflow: hidden; }\n");
        html.append("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }\n");
        html.append("        .header h1 { font-size: 32px; margin-bottom: 10px; }\n");
        html.append("        .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; padding: 30px; background: #f8f9fa; }\n");
        html.append("        .stat-box { background: white; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #667eea; }\n");
        html.append("        .stat-box h3 { color: #667eea; font-size: 24px; margin-bottom: 5px; }\n");
        html.append("        .stat-box p { color: #666; font-size: 14px; }\n");
        html.append("        .stat-box.passed { border-left-color: #28a745; }\n");
        html.append("        .stat-box.passed h3 { color: #28a745; }\n");
        html.append("        .stat-box.failed { border-left-color: #dc3545; }\n");
        html.append("        .stat-box.failed h3 { color: #dc3545; }\n");
        html.append("        .content { padding: 30px; }\n");
        html.append("        .section { margin-bottom: 30px; }\n");
        html.append("        .section h2 { color: #667eea; font-size: 20px; margin-bottom: 15px; border-bottom: 2px solid #667eea; padding-bottom: 10px; }\n");
        html.append("        .test-case { background: #f8f9fa; padding: 15px; margin-bottom: 10px; border-radius: 5px; border-left: 4px solid #ddd; }\n");
        html.append("        .test-case.passed { border-left-color: #28a745; }\n");
        html.append("        .test-case.failed { border-left-color: #dc3545; }\n");
        html.append("        .test-name { font-weight: bold; color: #333; }\n");
        html.append("        .test-status { display: inline-block; padding: 5px 10px; border-radius: 3px; font-size: 12px; margin-top: 5px; }\n");
        html.append("        .test-status.passed { background: #d4edda; color: #155724; }\n");
        html.append("        .test-status.failed { background: #f8d7da; color: #721c24; }\n");
        html.append("        .test-message { font-size: 12px; color: #666; margin-top: 5px; }\n");
        html.append("        .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 12px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>🧪 PigletV2 QA Test Report</h1>\n");
        html.append("            <p>Automated Testing Framework for Enterprise QA</p>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"summary\">\n");
        html.append(String.format("            <div class=\"stat-box\"><h3>%d</h3><p>Total Tests</p></div>\n", results.getTotalTests()));
        html.append(String.format("            <div class=\"stat-box passed\"><h3>%d</h3><p>Passed</p></div>\n", results.getPassedTests()));
        html.append(String.format("            <div class=\"stat-box failed\"><h3>%d</h3><p>Failed</p></div>\n", results.getFailedTests()));
        html.append(String.format("            <div class=\"stat-box\"><h3>%.1f%%</h3><p>Success Rate</p></div>\n", 
            results.getTotalTests() > 0 ? (results.getPassedTests() * 100.0 / results.getTotalTests()) : 0));
        html.append("        </div>\n");
        html.append("        <div class=\"content\">\n");
        html.append("            <div class=\"section\">\n");
        html.append("                <h2>📊 BeanShell Script Test Results</h2>\n");
        
        for (TestResult test : results.getBeanShellResults()) {
            html.append(formatTestCase(test));
        }

        html.append("            </div>\n");
        html.append("            <div class=\"section\">\n");
        html.append("                <h2>🔍 SPARQL Query Execution Results</h2>\n");
        
        for (TestResult test : results.getSparqlResults()) {
            html.append(formatTestCase(test));
        }

        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"footer\">\n");
        html.append(String.format("            <p>Generated on: %s</p>\n", timestamp));
        html.append(String.format("            <p>Execution Time: %d ms</p>\n", executionTime));
        html.append("            <p>PigletV2 - Enterprise QA Framework</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        String outputPath = OUTPUT_DIR + "/piglet-test-report.html";
        Files.write(Paths.get(outputPath), html.toString().getBytes());
        System.out.println("✓ HTML Report saved to: " + outputPath);
    }

    private static String formatTestCase(TestResult test) {
        String statusClass = test.isPassed() ? "passed" : "failed";
        String statusText = test.isPassed() ? "✓ PASSED" : "✗ FAILED";
        
        return String.format(
            "            <div class=\"test-case %s\">\n" +
            "                <div class=\"test-name\">%s</div>\n" +
            "                <span class=\"test-status %s\">%s</span>\n" +
            "                <div class=\"test-message\">%s</div>\n" +
            "            </div>\n",
            statusClass, test.getName(), statusClass, statusText, test.getMessage()
        );
    }

    static class TestResults {
        private List<TestResult> beanShellResults = new ArrayList<>();
        private List<TestResult> sparqlResults = new ArrayList<>();

        void addBeanShellResult(TestResult result) {
            beanShellResults.add(result);
        }

        void addSparqlResult(TestResult result) {
            sparqlResults.add(result);
        }

        void addAll(List<TestResult> results) {
            for (TestResult result : results) {
                if (result.getType().equals("BEANSHELL")) {
                    addBeanShellResult(result);
                } else {
                    addSparqlResult(result);
                }
            }
        }

        int getTotalTests() {
            return beanShellResults.size() + sparqlResults.size();
        }

        int getPassedTests() {
            return (int) (beanShellResults.stream().filter(TestResult::isPassed).count() +
                         sparqlResults.stream().filter(TestResult::isPassed).count());
        }

        int getFailedTests() {
            return getTotalTests() - getPassedTests();
        }

        List<TestResult> getBeanShellResults() {
            return beanShellResults;
        }

        List<TestResult> getSparqlResults() {
            return sparqlResults;
        }
    }

    static class TestResult {
        private String name;
        private boolean passed;
        private String message;
        private String type;

        public TestResult(String name, boolean passed, String message, String type) {
            this.name = name;
            this.passed = passed;
            this.message = message;
            this.type = type;
        }

        String getName() { return name; }
        boolean isPassed() { return passed; }
        String getMessage() { return message; }
        String getType() { return type; }
    }
}
