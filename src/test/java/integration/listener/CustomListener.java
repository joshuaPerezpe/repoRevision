package integration.listener;

import cl.bice.automationqa.azure.controllers.IndexController;
import cl.bice.automationqa.azure.controllers.TestResultsController;
import cl.bice.automationqa.azure.publish.azure_class.test_result_detail.RunnerActionResults;
import cl.bice.automationqa.azure.publish.azure_class.test_result_detail.RunnerTestResult;
import cl.bice.automationqa.azure.publish.defect.Bug;
import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class CustomListener implements RuntimeHook {
    private final Logger logger = LoggerFactory.getLogger(CustomListener.class);
    private HashMap<String,String> exampleAndFeatureNameMap;
    private HashMap<String, String> bugMap;
    private boolean publishOnADO;
    private boolean createOnADO;
    private IndexController indexController;
    public CustomListener(boolean createOnADO, boolean publishOnADO, IndexController indexController1){
        this.exampleAndFeatureNameMap = new HashMap<>();
        this.publishOnADO = publishOnADO;
        this.createOnADO = createOnADO;
        this.indexController = indexController1;
        this.bugMap = new HashMap<>();
    }

    public HashMap<String, String> getExampleAndFeatureNameMap(){
        return this.exampleAndFeatureNameMap;
    }

    public TestResultsController getTestResultsController() {
        return this.indexController.getTestResultsController();
    }

    public HashMap<String,String> getBugMap() {
        return bugMap;
    }

    /**
     * NO CONSIDERA SCENARIOS QUE NO CONTENGAN JSON
     * @param fr
     */
    @Override
    public void afterFeature(FeatureRuntime fr) {

        if(createOnADO){
            fr.result.getScenarioResults().forEach(x->{
                if (x.getScenario().isOutlineExample()) {
                    String dinamicExpresion =  x.getScenario().getDynamicExpression();
                    String scenarioExamplePath = dinamicExpresion.substring(dinamicExpresion.indexOf(":")+1,dinamicExpresion.indexOf(")")+-1);
                    String scenarioName = x.getScenario().getName();
                    logger.info("ESCENARIO : " + scenarioName);
                    logger.info("Example : " + scenarioExamplePath);
                    if (!scenarioExamplePath.isEmpty()) {
                        scenarioName = scenarioName.contains("_") ? scenarioName.split("_")[1] : scenarioName;
                        this.exampleAndFeatureNameMap.put(scenarioName, scenarioExamplePath);
                    } else {
                        logger.info("Creación - El scenario no posee example en el formato permitido ( JSON)");
                        logger.info("Se ignora el scenario :" + scenarioName);
                    }

                }
            });

        }
        RuntimeHook.super.afterFeature(fr);
    }

    @Override
    public void afterScenario(ScenarioRuntime sr) {


        if(publishOnADO &&  sr.scenario.getName().contains("_")){
            List<RunnerActionResults> actionResultsCollection = new ArrayList<>();
            String featureName = sr.scenario.getName();
            String idAzure = featureName.split("_")[0].trim();
            String failedStepLog = null;
            if (sr.result.isFailed()) {
                failedStepLog = sr.result.getFailedStep().getStepLog();
            }
            long starTime = sr.result.getStartTime();
            long endTime = sr.result.getEndTime();

            int counterStep = 1;
            String dateStart = "2024-08-01T17:08:14";
            List<StepResult> steps = sr.result.getStepResults();



            for (StepResult step : steps) {
                counterStep++;
                RunnerActionResults runnerActionResults = new RunnerActionResults();
                runnerActionResults.setActionPath(generateNextActionPath(counterStep));
                String result = step.getResult().getStatus();
                String outcome = result.substring(0,1).toUpperCase() + result.substring(1).toLowerCase();
                outcome = outcome.compareToIgnoreCase("skipped") == 0 ? "Aborted": outcome;
                runnerActionResults.setOutcome(outcome);
                runnerActionResults.setCompletedDate(dateStart);
                runnerActionResults.setCompletedDate(dateStart);
                runnerActionResults.setIterationId(1);
                runnerActionResults.setStepIdIdentifier(String.valueOf(counterStep));
                actionResultsCollection.add(runnerActionResults);

            }
            logger.debug("Publicación - IDAZURE RESCATADO "+idAzure);
            boolean createBug = Boolean.parseBoolean(System.getProperty("createBug"));
            indexController.addTestResult(idAzure, actionResultsCollection, failedStepLog);

            if(createBug){
                String tag = sr.scenario.getTags().get(0).getName();
                String errorMessage = sr.result.getErrorMessage() == null ? "" : sr.result.getErrorMessage();

                try {
                    afterScenarioCreateBug(idAzure, featureName, tag, steps.toString(), errorMessage);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

        }
        RuntimeHook.super.afterScenario(sr);
    }


    public void afterScenarioCreateBug(String idAzure, String featureName, String tag, String steps, String errorMessage ) throws Throwable {
        try {


            String title = featureName.split("_")[1] + " - defect";
            String email = System.getenv("USER_EMAIL");
            String description = "defecto producido desde framework";
            String comment = "";
            String env = System.getProperty("karate.env");
            String bugType = "Error QA";
            String idBug = "";

            ArrayList<String> bugList = indexController.getDefectTicketsForTestWorkItem(idAzure);

            if(!bugList.isEmpty()) {

                for (String workItem : bugList) {

                    if(!indexController.getStateWorkItem(workItem).equalsIgnoreCase("closed")){
                        bugMap.put(idAzure, workItem);
                    }
                }

                if(bugMap.get(idAzure)==null && !errorMessage.isEmpty()){
                    idBug =  createBug(tag, title, email, steps, description, "1", "4 - Low", "1", env, bugType, "Testing", idAzure, comment);
                    logger.debug("Creación Defecto - Creado : idAdo : "+idAzure+ " IDBUG:"+ idBug);
                    bugMap.put(idAzure, idBug);
                }
            }


            if (bugList.isEmpty() && !errorMessage.isEmpty()) {

                idBug =  createBug(tag, title, email, steps, description, "1", "4 - Low", "1", env, bugType, "Testing", idAzure, comment);
                logger.debug("Creación Defecto - Creado : idAdo : "+idAzure+ " IDBUG:"+ idBug);
                bugMap.put(idAzure, idBug);
            }



        } catch (IOException | InterruptedException e) {
            throw new Throwable (e.getMessage());
        }
    }

        /// metodos utilizados en integrationtest
    public  void updateTestPointOnSuiteByList (IndexController controller) throws FileNotFoundException {

        List<String> listado = listarKarateJson();
        for (int i = 0; i < listado.size(); ++i) {
            controller.updateTestPointOnSuiteByList(listado);
        }
    }

    public String createBug(String tag,String title,String email,String steps,String description,String priority,String severity, String razonBlo,String env,String bugType, String activityType, String idAzure, String comment) throws IOException, IllegalAccessException, InterruptedException {
        String idBug;
        Bug bug = indexController.createBug(tag, title, email, steps, description, priority, severity, razonBlo, env, bugType, activityType);
        String jsonBody = indexController.createBugItemBody(bug, idAzure, comment);
        idBug = String.valueOf(indexController.createBugOnADO(jsonBody));

        return  idBug;
    }

    public void linkedBugWithRunnerResult(ConcurrentHashMap<String, RunnerTestResult> runnerTestResultHashMap, String runnerId, HashMap<String, String> bugMapSeted){
        try{
            runnerTestResultHashMap.forEach((k,v)->{
                String errorMessage = v.getErrorMessage();
                String idBug = bugMapSeted.get(k);
                if(idBug!=null || errorMessage!=null){
                    int resultId = v.getId();
                    indexController.linkedBugWithRunnerResult(Integer.parseInt(runnerId),resultId,Integer.parseInt(idBug));
                }

            });

        }catch (Exception ex){
            logger.debug("Creación defecto - Ha ocurrido un error al intentar crear un Bug.");
            logger.debug("Error : "+ex.getMessage());
        }

    }

    public void changeIdOnFeatureFile(String filePath) throws IOException {
        try{
            filePath = filePath.replace(".karate-json.txt","").replace(".","/").trim();
            filePath ="src/test/java/"+ filePath ;
            logger.debug("PATH : "+filePath);
            String content = Files.readString(Path.of(filePath+".feature"), StandardCharsets.UTF_8);
            if(!content.contains("idAzure")){
                content = content.replaceAll("(?i)IDJIRA|ID" ,"idAzure");
            }
            content = "@ignore "+content;
            Files.writeString(Path.of(filePath+".feature"), content,StandardCharsets.UTF_8);

        }catch (IOException | IllegalArgumentException | UnsupportedOperationException   ex){
            logger.debug("Ah ocurrido un error al intentar escribir el nuevo archivo feature.");
            logger.debug("Error : "+ex.getMessage());
        }

    }

    public static List<String> listarKarateJson() {
        List<String> results = new ArrayList();
        String PROJECT = System.getProperty("user.dir");
        String SEP = System.getProperty("file.separator");
        String folder = PROJECT + SEP + "target" + SEP + "karate-reports";
        File carpeta = new File(folder);
        String[] listado = carpeta.list();
        if (listado != null && listado.length != 0) {
            for (int i = 0; i < listado.length; ++i) {
                if (listado[i].contains("karate-json.txt")) {
                    results.add(listado[i]);
                }
            }
        } else {
            System.out.println("No existen elementos dentro de la carpeta karate-reports");
        }

        return results;
    }

    private String generateNextActionPath(int num) {
        String actionPath = Integer.toHexString(num).toLowerCase(Locale.ROOT);
        while (actionPath.length() < 8) {
            actionPath = "0" + actionPath;
        }
        return actionPath;
    }

}

