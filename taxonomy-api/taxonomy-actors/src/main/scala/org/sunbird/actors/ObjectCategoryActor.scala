package org.sunbird.actors

import java.util
import javax.inject.Inject
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}
import org.sunbird.actor.core.BaseActor
import org.sunbird.cache.impl.RedisCache
import org.sunbird.common.Slug
import org.sunbird.common.dto.{Request, Response, ResponseHandler}
import org.sunbird.common.exception.ClientException
import org.sunbird.graph.OntologyEngineContext
import org.sunbird.graph.nodes.DataNode
import org.sunbird.graph.utils.NodeUtil
import org.sunbird.utils.{Constants, RequestUtil}

import scala.collection.JavaConverters
import scala.concurrent.{ExecutionContext, Future}

class ObjectCategoryActor @Inject()(implicit oec: OntologyEngineContext) extends BaseActor {

    implicit val ec: ExecutionContext = getContext().dispatcher

    private val logger: Logger = LoggerFactory.getLogger(RedisCache.getClass.getCanonicalName)
    override def onReceive(request: Request): Future[Response] = {
        request.getOperation match {
            case Constants.CREATE_OBJECT_CATEGORY => create(request)
            case Constants.READ_OBJECT_CATEGORY => read(request)
            case Constants.UPDATE_OBJECT_CATEGORY => update(request)
            case Constants.UPLOAD_DATA_OBJECT_CATEGORY => upload(request)
            case _ => ERROR(request.getOperation)
        }
    }


    @throws[Exception]
    private def create(request: Request): Future[Response] = {
        RequestUtil.restrictProperties(request)
        if (!request.getRequest.containsKey(Constants.NAME)) throw new ClientException("ERR_NAME_SET_AS_IDENTIFIER", "name will be set as identifier")
        request.getRequest.put(Constants.IDENTIFIER, Constants.CATEGORY_PREFIX + Slug.makeSlug(request.getRequest.get(Constants.NAME).asInstanceOf[String]))
        DataNode.create(request).map(node => {
            ResponseHandler.OK.put(Constants.IDENTIFIER, node.getIdentifier)
        })
    }

    @throws[Exception]
    private def read(request: Request): Future[Response] = {
        val fields: util.List[String] = JavaConverters.seqAsJavaListConverter(request.get(Constants.FIELDS).asInstanceOf[String].split(",").filter(field => StringUtils.isNotBlank(field) && !StringUtils.equalsIgnoreCase(field, "null"))).asJava
        request.getRequest.put(Constants.FIELDS, fields)
        DataNode.read(request).map(node => {Future
            val metadata: util.Map[String, AnyRef] = NodeUtil.serialize(node, fields, request.getContext.get(Constants.SCHEMA_NAME).asInstanceOf[String], request.getContext.get(Constants.VERSION).asInstanceOf[String])
            ResponseHandler.OK.put(Constants.OBJECT_CATEGORY, metadata)
        })
    }

    @throws[Exception]
    private def update(request: Request): Future[Response] = {
        RequestUtil.restrictProperties(request)
        DataNode.update(request).map(node => {
            ResponseHandler.OK.put(Constants.IDENTIFIER, node.getIdentifier)
        })
    }

    @throws[Exception]
    private def upload(request: Request): Future[Response] = {
//        logger.info("Start the upload method")
//        competencyObj.setCompetencyCode("C1")
//        competencyObj.setSubject("Midwifery & Gynecological Nursing")
//        competencyObj.setCompetencyLabel("label")
//        competencyObj.setCompetencyDescription("Description")
//        competencyObj.setCompetencyLevel1Label("L1Label")
//        competencyObj.setCompetencyLevel1Description("L1Desc")
//        competencyObj.setCompetencyLevel2Label("L2Label")
//        competencyObj.setCompetencyLevel2Description("L2Desc")
//        competencyObj.setCompetencyLevel3Label("L3Label")
//        competencyObj.setCompetencyLevel3Description("L3Desc")
//        competencyObj.setCompetencyLevel4Label("L4Label")
//        competencyObj.setCompetencyLevel4Description("L4Desc")
//        competencyObj.setCompetencyLevel5Label("L5Label")
//        competencyObj.setCompetencyLevel5Description("L5Desc")
//        logger.info("After setting all competencies")
//        competencyRepository.save(competencyObj)
        Future.apply(ResponseHandler.OK.put(Constants.IDENTIFIER,"12345"))
    }

}
