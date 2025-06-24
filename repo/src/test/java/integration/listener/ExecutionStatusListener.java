package integration.listener;


import com.intuit.karate.Results;
import com.intuit.karate.core.FeatureResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExecutionStatusListener  {

    public String featureName;
    private String tag;
    private String branch;
    private String parentSuiteId;
    private String planId;

    public String project;

    public String suiteName;

    private final HashMap<String, String> featureIdAzureMap;

    private final Logger logger = LoggerFactory.getLogger(ExecutionStatusListener.class);


    public ExecutionStatusListener(String featureName, String tag, String branch, String parentSuiteId, String planId, String project) {
        this.featureName = featureName;
        this.tag = tag;
        this.branch = branch;
        this.parentSuiteId = parentSuiteId;
        this.planId = planId;
        this.featureIdAzureMap = new HashMap<>();
        this.project = project;
    }

    public ExecutionStatusListener(Results results) {
        this.featureIdAzureMap = new HashMap<>();
        this.tag=System.getProperty("karate.options").split("@")[1];
        this.project = System.getenv("PROJECT");
        this.parentSuiteId = System.getProperty("parentSuite");
        this.planId = System.getProperty("planId");
        this.branch = getBranchActive();
        this.suiteName = System.getProperty("suiteName");
        FeatureResult featureResult = results.getFeatureResults().collect(Collectors.toList()).get(0);
        this.featureName = featureResult.getFeature().getName();

    }

    public void setFeatureIdAzureMap(HashMap<String, String> featureIdAzureMap) {
        this.featureIdAzureMap.putAll(featureIdAzureMap);
    }


    public String getBranchActive() {
        try {
            Path headPath = Path.of(".git", "HEAD");
            String headContent = Files.readString(headPath).trim();
            if (headContent.startsWith("ref:")) {
                return headContent.substring(headContent.lastIndexOf("/") + 1);
            } else {
                logger.info("HEAD está apuntando a un commit específico: " + headContent);
            }
        } catch (IOException e) {
            logger.info("No se pudo leer el archivo .git/HEAD: " + e.getMessage());
        }
        return null;
    }

    public String printCreacionDetail(){
        StringBuilder builder = new StringBuilder();
        builder.append("===========================================================").append("\n");

        builder.append("Fature name : ").append(this.featureName).append("\n");
        builder.append("Suite name : ").append(this.suiteName).append("\n");

        builder.append("Tag : ").append(this.tag).append("\n");
        builder.append("Branch : ").append(this.branch).append("\n");
        builder.append("Parent Suite ID : ").append(this.parentSuiteId).append("\n");
        builder.append("Plan ID : ").append(this.planId).append("\n");
        builder.append("Proyecto : ").append(this.project).append("\n");

        featureIdAzureMap.forEach((key, value) -> builder.append("Test Case name : ").append(key).append("\n").append("Test case ID Azure : ").append(value).append("\n"));
        builder.append("===========================================================").append("\n");

        return  builder.toString();
    }





}
