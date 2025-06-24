package integration;

import cl.bice.automationqa.azure.controllers.IndexController;
import cl.bice.automationqa.azure.create.azure_class.WorkItem;
import cl.bice.automationqa.azure.publish.azure_class.test_result_detail.RunnerTestResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import integration.listener.CustomListener;
import integration.listener.ExecutionStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

	private final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
	public HashMap<String,String> exampleAndFeatureNameMap = new HashMap<>();
	private IndexController indexController;
	private int countFails;
	private final String planId;
	private final String suiteId;
	private final boolean publishOnADO;
	private final boolean createOnADO;
	private final boolean createBug;
	private final String suiteName;
	private CustomListener customListener;

	private ExecutionStatusListener executionStatusListener;
	public IntegrationTest() throws IOException {
		this.planId = System.getProperty("planId");
		this.suiteId = System.getProperty("suiteId");
		this.createBug = Boolean.parseBoolean(System.getProperty("createBug"));
		this.publishOnADO = Boolean.parseBoolean(System.getProperty("azure"));
		this.createOnADO = Boolean.parseBoolean(System.getProperty("create"));
		this.suiteName = System.getProperty("suiteName");
		this.indexController = new IndexController();
	}

	@Test
	void testParallel() throws IOException, InterruptedException {
		Results results = Runner.path("classpath:integration")
				.outputJunitXml(true)
				.outputCucumberJson(true)
				.hook(new CustomListener(createOnADO, publishOnADO, indexController))
				.parallel(20);

		executionStatusListener = new ExecutionStatusListener(results);

		customListener = (CustomListener) results.getSuite().hooks.toArray()[0];

		if (publishOnADO) {
			countFails += results.getFailCount();
			customListener.updateTestPointOnSuiteByList(indexController);
			boolean addTestPointToSuite = indexController.publishTestPointOnSuite(planId, suiteId);
			assertTrue(addTestPointToSuite,"Publicación - No se logro agregar testpoint a la suite.");

			String runnerId = indexController.getRunnerIdFromSuite(planId, suiteId);
			assertNotNull(runnerId,"Publicación - El ID RUNNER obtenido es nulo.");

			JsonNode getValueRunnerData = indexController.getRunnerData(runnerId);
			assertNotNull(getValueRunnerData,"Publicación - Se se logro obtener información del RUNNER.");
			ConcurrentHashMap<String, RunnerTestResult> runnerTestResultHashMap = indexController.mapTestResulsWithTestExecuted(getValueRunnerData,runnerId,customListener.getTestResultsController());

			if(createBug){

				HashMap<String, String> bugMap = customListener.getBugMap();

				if(!bugMap.isEmpty()){
					customListener.linkedBugWithRunnerResult(runnerTestResultHashMap, runnerId, bugMap);

				}
			}

			boolean addStepsToResult = indexController.pushStepsToTestCase(runnerId,runnerTestResultHashMap.values().toArray(new RunnerTestResult[0]));
			assertTrue(addStepsToResult,"Publicación - No se lograron agregar los steps de los resultados en suite.");

		}

		if(createOnADO){
			this.exampleAndFeatureNameMap =customListener.getExampleAndFeatureNameMap();
		}

		assertEquals(0, results.getFailCount(), results.getErrorMessages());

	}
	/**
	 * Get suite on ADO
	 */
	@BeforeSuite
	void getSuiteByPlanIdSuiteId(ITestContext context) {
		if (publishOnADO && !planId.isEmpty() && !suiteId.isEmpty()) {
			logger.info("Publicación - Obteniendo suite de ADO.");
			indexController.getSuite(planId, suiteId);
		}
	}
	/**
	 * createOnADO a zip file with report on ADO
	 *
	 * @throws IOException
	 */
	@AfterSuite()
	void zipKarateReport() throws IOException {
		if (publishOnADO && !planId.isEmpty() && !suiteId.isEmpty()) {
			logger.info("Publicación - Comprimiendo reporte de Karate.");
			indexController.zipKarateReport();
		}

	}
	/**
	 * Set on runner report on ADO
	 */
	@AfterSuite(dependsOnMethods = "zipKarateReport")
	void setReportOnRunAzure() {
		if (publishOnADO && !planId.isEmpty() && !suiteId.isEmpty()) {
			logger.info("Publicación - Seteando detalle y reporte runner ADO.");
			indexController.setKarateReportOnAzure(countFails);
		}
	}
	/**
	 * Event orchestrator for createOnADO testcases and suite on azure devops
	 */
	@AfterSuite()
	void createOnAZure() throws Throwable {

		if (createOnADO){
			HashMap<String, String> testCasesCreatedOnAdo = new HashMap<>();
			List<String> listado = customListener.listarKarateJson();
			for (int i = 0; i < listado.size(); ++i) {
				try {
					/**
					 * createOnADO test case on azure format
					 */
					Set<List<WorkItem>> workItems = indexController.createTestCase(listado.get(i));
					/**
					 * createOnADO test case on azure
					 */
					testCasesCreatedOnAdo = indexController.createTestCaseOnAzure(this.exampleAndFeatureNameMap, workItems);
					/**
					 * Add ID azure on features with <id> tag or <idTestJira>. With tag azure devops
					 */

					customListener.changeIdOnFeatureFile(listado.get(i));
                    /* ADD test created to end detail*/
					executionStatusListener.setFeatureIdAzureMap(testCasesCreatedOnAdo);


				}catch (IOException | IllegalAccessException ex){
					logger.info("Creación testCase - Ha ocurrido un error al interactuar con resultados de karate.");
					logger.info("Error : "+ ex.getMessage());
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}


			}
			String parentSuiteId = System.getProperty("parentSuite","");

			if(!planId.isEmpty() && !parentSuiteId.isEmpty()){

				if(testCasesCreatedOnAdo==null){
					throw  new Throwable("Creación testCase - No se logro obtener y/o crear los casos de prueba en ADO.");
				}
				String idsTestCases = indexController.createStringWithTestCases(testCasesCreatedOnAdo);
				String newSuiteId = indexController.createSuite(planId,parentSuiteId,suiteName,"9");
				indexController.addTestToSuite(idsTestCases,planId,newSuiteId);
				/*Log detail test cases created*/
                logger.info(executionStatusListener.printCreacionDetail());

			}else {
				logger.info("Creación testCase - Falta el parametro PLAN_ID o PARENT_SUITE_ID.");
			}


		}
	}

}