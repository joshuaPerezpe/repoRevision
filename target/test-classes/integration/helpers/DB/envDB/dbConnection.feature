Feature: Utility Conection to Database

  Background:
    * def config = read('classpath:integration/helpers/DB/envDB/config.json')
    * def DbUtils = Java.type('integration.helpers.DB.envDB.DbUtils')

  @orawqa_por_exp
  Scenario:
    * def connect = new DbUtils(config.orawqa_por_exp)

  @orawpor_por_admin
  Scenario:
    * def connect = new DbUtils(config.orawpor_por_admin)

  @orawqa_msl_exp
  Scenario:
    * def connect = new DbUtils(config.orawqa_msl_exp)

  @scoqas_scr_exp
  Scenario:
    * def connect = new DbUtils(config.scoqas_scr_exp)

  @oraplaqa_prv_exp
  Scenario:
    * def connect = new DbUtils(config.oraplaqa_prv_exp)

  @oraldes1_pas_admin
  Scenario:
    * def connect = new DbUtils(config.oraldes1_pas_admin)

  @orawqa_cap_exp
  Scenario:
    * def connect = new DbUtils(config.orawqa_cap_exp)

  @orawpor_cap_exp
  Scenario:
    * def connect = new DbUtils(config.orawpor_cap_exp)

  @oraplaqa_cif_exp
  Scenario:
    * def connect = new DbUtils(config.oraplaqa_cif_exp)

  @oraplaqa_pas_exp
  Scenario:
    * def connect = new DbUtils(config.oraplaqa_pas_exp)

  @orawqa_jrn_exp
  Scenario:

    * def connect = new DbUtils(config.gke_track_dept_plz)

  @orawqa_jrn_exp
  Scenario:
    * def connect = new DbUtils(config.orawqa_jrn_exp)

  