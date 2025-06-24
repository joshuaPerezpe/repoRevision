Feature: Utility Execute Query to Database

  Background:
    * def orawqa = call read('classpath:integration/helpers/DB/envDB/dbConnection.feature@orawqa_por_exp')
    * def execute = orawqa.connect

  @tbl_oper_prog_consulta
  Scenario:
    * def selectQuery = read('classpath:integration/helpers/DB/flowDB/apiBaasLbtr/querys/select_tbl_oper_prog_by_num_oper_prog.txt')
    * replace selectQuery.operprog = operprog
    * def tbl_oper_prog = execute.readRows(selectQuery)

  @tbl_detalle_camp_consulta
  Scenario:
    * def selectQuery = read('classpath:integration/helpers/DB/flowDB/apiBaasLbtr/querys/select_tbl_detalle_camp_by_num_oper_prog.txt')
    * replace selectQuery.operprog = operprog
    * def tbl_detalle_camp = execute.readRows(selectQuery)
