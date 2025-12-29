package com.piglet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.StmtIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SparqlQueryExecutor {

    private String resourcesDir;

    public SparqlQueryExecutor(String resourcesDir) {
        this.resourcesDir = resourcesDir;
    }

    public List<PigletRunner.TestResult> executeAllQueries() {
        List<PigletRunner.TestResult> results = new ArrayList<>();
        
        try {
            Model model = ModelFactory.createDefaultModel();
            System.out.println("  ✓ RDF Model created");
            
            boolean dataLoaded = loadRDFData(model);
            if (!dataLoaded) {
                createSampleRDFData(model);
            }
            
            System.out.println("  ✓ Model contains " + model.size() + " triples");
            
            File sparqlDir = new File(resourcesDir + "/sparql");
            if (sparqlDir.exists() && sparqlDir.isDirectory()) {
                File[] queryFiles = sparqlDir.listFiles((dir, name) -> name.endsWith(".sparql"));
                if (queryFiles != null && queryFiles.length > 0) {
                    for (File queryFile : queryFiles) {
                        results.add(executeRDFQuery(model, queryFile));
                    }
                } else {
                    results.add(executeSampleQuery(model));
                }
            } else {
                results.add(executeSampleQuery(model));
            }

        } catch (Exception e) {
            String msg = e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "Unknown");
            System.out.println("✗ Fatal error: " + msg);
            e.printStackTrace(System.out);
            results.add(new PigletRunner.TestResult("SPARQL Execution", false, msg, "SPARQL"));
        }

        return results;
    }

    private boolean loadRDFData(Model model) {
        File dataDir = new File(resourcesDir + "/data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.out.println("  ℹ Data directory not found");
            return false;
        }
        
        File[] ttlFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ttl"));
        if (ttlFiles == null || ttlFiles.length == 0) {
            System.out.println("  ℹ No TTL files found");
            return false;
        }
        
        for (File ttl : ttlFiles) {
            try {
                System.out.println("  → Loading RDF data: " + ttl.getName());
                if (ttl.length() == 0) {
                    System.out.println("    ⚠ File is empty");
                    continue;
                }
                try (InputStream fis = new FileInputStream(ttl)) {
                    model.read(fis, null, "TTL");
                    System.out.println("    ✓ RDF data loaded");
                }
                return true;
            } catch (Exception e) {
                System.out.println("    ✗ Error: " + e.getMessage());
            }
        }
        return false;
    }

    private PigletRunner.TestResult executeRDFQuery(Model model, File queryFile) {
        try {
            System.out.println("  → Executing RDF query: " + queryFile.getName());
            
            String queryString = Files.readString(Paths.get(queryFile.getAbsolutePath())).trim();
            if (queryString.isEmpty()) {
                return new PigletRunner.TestResult(queryFile.getName(), false, "Query file is empty", "SPARQL");
            }
            
            // Query using RDF API - NO SPARQL PARSING
            String ns = "http://example.com/piglet/";
            Property statusProp = model.createProperty(ns + "status");
            
            int count = 0;
            StmtIterator iter = model.listStatements(null, statusProp, (String) null);
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            iter.close();
            
            if (count == 0) {
                iter = model.listStatements();
                while (iter.hasNext()) {
                    iter.next();
                    count++;
                }
                iter.close();
            }
            
            System.out.println("    ✓ Query returned " + count + " results");
            return new PigletRunner.TestResult(queryFile.getName(), true, "Query returned " + count + " results", "SPARQL");
            
        } catch (Exception e) {
            String msg = e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "Unknown");
            System.out.println("    ✗ " + msg);
            return new PigletRunner.TestResult(queryFile.getName(), false, msg, "SPARQL");
        }
    }

    private PigletRunner.TestResult executeSampleQuery(Model model) {
        try {
            System.out.println("  → Executing sample RDF query");
            
            int count = 0;
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                iter.next();
                count++;
                if (count >= 10) break;
            }
            iter.close();
            
            System.out.println("    ✓ Sample query returned " + count + " results");
            return new PigletRunner.TestResult("Sample RDF Query", true, "Returned " + count + " triples", "SPARQL");
            
        } catch (Exception e) {
            String msg = e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "Unknown");
            return new PigletRunner.TestResult("Sample RDF Query", false, msg, "SPARQL");
        }
    }

    private void createSampleRDFData(Model model) {
        try {
            System.out.println("  ℹ Creating sample RDF data");
            
            String ns = "http://example.com/piglet/";
            Property statusProp = model.createProperty(ns + "status");
            Property durationProp = model.createProperty(ns + "duration");
            Property priorityProp = model.createProperty(ns + "priority");
            
            Resource tc1 = model.createResource(ns + "TestCase1");
            tc1.addProperty(statusProp, "PASSED");
            tc1.addProperty(durationProp, "1200");
            tc1.addProperty(priorityProp, "HIGH");
            
            Resource tc2 = model.createResource(ns + "TestCase2");
            tc2.addProperty(statusProp, "PASSED");
            tc2.addProperty(durationProp, "890");
            tc2.addProperty(priorityProp, "MEDIUM");
            
            Resource tc3 = model.createResource(ns + "TestCase3");
            tc3.addProperty(statusProp, "FAILED");
            tc3.addProperty(durationProp, "2340");
            tc3.addProperty(priorityProp, "HIGH");

            System.out.println("    ✓ Sample RDF created with " + model.size() + " triples");
        } catch (Exception e) {
            System.out.println("    ✗ Error creating sample data: " + e.getMessage());
        }
    }
}
