package controllers.v4

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Singleton
import controllers.BaseController
import models.Competency
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.namedparam.{MapSqlParameterSource, NamedParameterJdbcTemplate, SqlParameterSource}
import org.springframework.jdbc.support.{GeneratedKeyHolder, KeyHolder}
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.sunbird.cache.impl.RedisCache

import javax.inject.{Inject, Named}
import org.sunbird.utils.Constants
import play.api.mvc.ControllerComponents
import repository.CompetencyRepositoryTrait
import service.CompetencyService
import utils.{ActorNames, ApiId}

import java.lang
import java.util.Optional
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

@Singleton
@EnableJpaRepositories
@EnableTransactionManagement
class ObjectCategoryController @Inject()(@Named(ActorNames.OBJECT_CATEGORY_ACTOR) objectCategoryActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem, competencyObj: Competency)(implicit exec: ExecutionContext) extends BaseController(cc) {

//    @Autowired
//    private val competencyRepositoryLocal:CompetencyRepository = new ObjectCategoryController(objectCategoryActor, cc, actorSystem, competencyObj)

    private val logger: Logger = LoggerFactory.getLogger(RedisCache.getClass.getCanonicalName)

    //final val sql: String = "insert into employee(employeeId, employeeName , employeeAddress,employeeEmail) values(:employeeId,:employeeName,:employeeEmail,:employeeAddress)";
    final val sql: String = "INSERT INTO taxonomy.competency (competency_code, subject, competency_label, competency_description, competency_level_1_label, competency_level_1_description, competency_level_2_label, competency_level_2_description, competency_level_3_label, competency_level_3_description, competency_level_4_label, competency_level_4_description, competency_level_5_label, competency_level_5_description) VALUES('', '', '', '', '', '', '', '', '', '', '', '', '', '');"

    val objectType = "ObjectCategory"

    @Inject
    var namedParameterJdbcTemplate: NamedParameterJdbcTemplate = null
    def this(objectCategoryActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem, competencyObj: Competency, namedParameterJdbcTemplate: NamedParameterJdbcTemplate)(implicit exec: ExecutionContext) {
        this(objectCategoryActor,cc,actorSystem,competencyObj)
        println("this namedParameterJdbcTemplate is "+this.namedParameterJdbcTemplate)
        println("namedParameterJdbcTemplate is "+namedParameterJdbcTemplate)
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate
        println("again this namedParameterJdbcTemplate is "+this.namedParameterJdbcTemplate)
    }


    def create() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val category = body.getOrDefault(Constants.OBJECT_CATEGORY, new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        category.putAll(headers)
        val categoryRequest = getRequest(category, headers, Constants.CREATE_OBJECT_CATEGORY)
        setRequestContext(categoryRequest, Constants.OBJECT_CATEGORY_SCHEMA_VERSION, objectType, Constants.OBJECT_CATEGORY_SCHEMA_NAME)
        getResult(ApiId.CREATE_OBJECT_CATEGORY, objectCategoryActor, categoryRequest)
    }

    def read(identifier: String, fields: Option[String]) = Action.async { implicit request =>
        val headers = commonHeaders()
        val category = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
        category.putAll(headers)
        category.putAll(Map(Constants.IDENTIFIER -> identifier, Constants.FIELDS -> fields.getOrElse("")).asJava)
        val categoryRequest = getRequest(category, headers,  Constants.READ_OBJECT_CATEGORY)
        setRequestContext(categoryRequest, Constants.OBJECT_CATEGORY_SCHEMA_VERSION, objectType, Constants.OBJECT_CATEGORY_SCHEMA_NAME)
        getResult(ApiId.READ_OBJECT_CATEGORY, objectCategoryActor, categoryRequest)
    }

    def update(identifier: String) = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val category = body.getOrDefault(Constants.OBJECT_CATEGORY, new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        category.putAll(headers)
        val categoryRequest = getRequest(category, headers,  Constants.UPDATE_OBJECT_CATEGORY)
        setRequestContext(categoryRequest, Constants.OBJECT_CATEGORY_SCHEMA_VERSION, objectType, Constants.OBJECT_CATEGORY_SCHEMA_NAME)
        categoryRequest.getContext.put(Constants.IDENTIFIER, identifier)
        getResult(ApiId.UPDATE_OBJECT_CATEGORY, objectCategoryActor, categoryRequest)
    }

    def uploadCompetency() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val category = body.getOrDefault(Constants.OBJECT_CATEGORY, new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        category.putAll(headers)
        val categoryRequest = getRequest(category, headers, Constants.UPLOAD_DATA_OBJECT_CATEGORY)
        setRequestContext(categoryRequest, Constants.OBJECT_CATEGORY_SCHEMA_VERSION, objectType, Constants.OBJECT_CATEGORY_SCHEMA_NAME)
        logger.info("Start the upload method")
        competencyObj.setCompetencyCode("C1")
        competencyObj.setSubject("Midwifery & Gynecological Nursing")
        competencyObj.setCompetencyLabel("label")
        competencyObj.setCompetencyDescription("Description")
        competencyObj.setCompetencyLevel1Label("L1Label")
        competencyObj.setCompetencyLevel1Description("L1Desc")
        competencyObj.setCompetencyLevel2Label("L2Label")
        competencyObj.setCompetencyLevel2Description("L2Desc")
        competencyObj.setCompetencyLevel3Label("L3Label")
        competencyObj.setCompetencyLevel3Description("L3Desc")
        competencyObj.setCompetencyLevel4Label("L4Label")
        competencyObj.setCompetencyLevel4Description("L4Desc")
        competencyObj.setCompetencyLevel5Label("L5Label")
        competencyObj.setCompetencyLevel5Description("L5Desc")
        logger.info("After setting all competencies")
        //competencyRepositoryLocal.save(competencyObj)
        //competencyService.save(competencyObj);
        new ObjectCategoryController(objectCategoryActor, cc, actorSystem, competencyObj, namedParameterJdbcTemplate)
        val holder : KeyHolder = new GeneratedKeyHolder();
        val param : SqlParameterSource = new MapSqlParameterSource()
          .addValue("competency_code", competencyObj.getCompetencyCode())
          .addValue("subject", competencyObj.getSubject())
          .addValue("competency_label", competencyObj.getCompetencyLabel())
          .addValue("competency_description", competencyObj.getCompetencyDescription())
          .addValue("competency_level_1_label", competencyObj.getCompetencyLevel1Label())
          .addValue("competency_level_1_description", competencyObj.getCompetencyLevel1Description())
          .addValue("competency_level_2_label", competencyObj.getCompetencyLevel2Label())
          .addValue("competency_level_2_description", competencyObj.getCompetencyLevel2Description())
          .addValue("competency_level_3_label", competencyObj.getCompetencyLevel3Label())
          .addValue("competency_level_3_description", competencyObj.getCompetencyLevel3Description())
          .addValue("competency_level_4_label", competencyObj.getCompetencyLevel4Label())
          .addValue("competency_level_4_description", competencyObj.getCompetencyLevel4Description())
          .addValue("competency_level_5_label", competencyObj.getCompetencyLevel5Label())
          .addValue("competency_level_5_description", competencyObj.getCompetencyLevel5Description())
        namedParameterJdbcTemplate.update(sql, param, holder);

        //logger.info("Competency service "+competencyService.findAll())
        getResult(ApiId.UPLOAD_DATA_OBJECT_CATEGORY, objectCategoryActor, categoryRequest)
    }

}
