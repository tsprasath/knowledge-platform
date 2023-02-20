//package handlers
//
//import models.Competency
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import org.slf4j.{Logger, LoggerFactory}
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.transaction.annotation.EnableTransactionManagement
//import org.sunbird.cache.impl.RedisCache
//import repository.CompetencyRepository
//
//import java.io.{File, FileInputStream}
//import java.lang
//import java.util.Optional
//import javax.inject.{Inject, Named}
//
//@EnableTransactionManagement
//@EnableJpaRepositories
//class CompetencyExcelParser @Inject()(competencyObj : Competency) extends CompetencyRepository {
//
//  @Autowired
//  private val competencyRepositoryLocal = new CompetencyExcelParser(competencyObj);
//
//  private val logger: Logger = LoggerFactory.getLogger(RedisCache.getClass.getCanonicalName)
//  def getCompetency(file: File) = {
//
//    try {
//      val workbook = new XSSFWorkbook(new FileInputStream(file))
//      val sheet = workbook.getSheetAt(0)
//      logger.info("Inside the get competency")
//      (1 until sheet.getPhysicalNumberOfRows)
//        .filter(rowNum => {
//          val oRow = Option(sheet.getRow(rowNum))
//          oRow match {
//            case Some(_) => {
//              val competencyCode = sheet.getRow(rowNum).getCell(0)
//              val subject = sheet.getRow(rowNum).getCell(1)
//              val competencyLabel = sheet.getRow(rowNum).getCell(2)
//              val competencyDescription = sheet.getRow(rowNum).getCell(3)
//              val competencyLevel1Label = sheet.getRow(rowNum).getCell(4)
//              val competencyLevel1Description = sheet.getRow(rowNum).getCell(5)
//              val competencyLevel2Label = sheet.getRow(rowNum).getCell(6)
//              val competencyLevel2Description = sheet.getRow(rowNum).getCell(7)
//              val competencyLevel3Label = sheet.getRow(rowNum).getCell(8)
//              val competencyLevel3Description = sheet.getRow(rowNum).getCell(9)
//              val competencyLevel4Label = sheet.getRow(rowNum).getCell(10)
//              val competencyLevel4Description = sheet.getRow(rowNum).getCell(11)
//              val competencyLevel5Label = sheet.getRow(rowNum).getCell(12)
//              val competencyLevel5Description = sheet.getRow(rowNum).getCell(13)
//              competencyObj.setCompetencyCode(competencyCode.toString)
//              competencyObj.setSubject(subject.toString)
//              competencyObj.setCompetencyLabel(competencyLabel.toString)
//              competencyObj.setCompetencyDescription(competencyDescription.toString)
//              competencyObj.setCompetencyLevel1Label(competencyLevel1Label.toString)
//              competencyObj.setCompetencyLevel1Description(competencyLevel1Description.toString)
//              competencyObj.setCompetencyLevel2Label(competencyLevel2Label.toString)
//              competencyObj.setCompetencyLevel2Description(competencyLevel2Description.toString)
//              competencyObj.setCompetencyLevel3Label(competencyLevel3Label.toString)
//              competencyObj.setCompetencyLevel3Description(competencyLevel3Description.toString)
//              competencyObj.setCompetencyLevel4Label(competencyLevel4Label.toString)
//              competencyObj.setCompetencyLevel4Description(competencyLevel4Description.toString)
//              competencyObj.setCompetencyLevel5Label(competencyLevel5Label.toString)
//              competencyObj.setCompetencyLevel5Description(competencyLevel5Description.toString)
//              competencyRepositoryLocal.save(competencyObj)
//              boolean2Boolean(true)
//            }
//            case None => false
//          }
//        }).toList
//    } catch {
//      case e : Exception => throw new Exception("Invalid File")
//    }
//  }
//
//}
