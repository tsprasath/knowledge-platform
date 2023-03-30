package handlers

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.{XSSFRow, XSSFSheet, XSSFWorkbook}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{File, FileInputStream}
import java.util
import scala.collection.JavaConverters.{asScalaIteratorConverter, iterableAsScalaIterableConverter, mapAsScalaMapConverter}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Activity(code: String, label: String)

object CompetencyExcelParser {

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  private var getData: List[java.util.Map[String,util.HashMap[String, AnyRef]]] = List.empty

  private var getFinalData: List[java.util.Map[String,AnyRef]] = List.empty

  private var listData :java.util.Map[String, AnyRef] = ?
  private var fracclMap: java.util.Map[String,util.HashMap[String, AnyRef]] = ?

  def parseCompetencyData(xssFRow: XSSFRow) = {

    val rowContent = (0 until xssFRow.getPhysicalNumberOfCells)
      .map(colId => Option(xssFRow.getCell(colId)).getOrElse("").toString).toList

    val function = rowContent.apply(0).trim
    val year = rowContent.apply(1).trim
    val roleId = rowContent.apply(2).trim
    val roleLabel = rowContent.apply(3).trim
    val competencyMapping = rowContent.apply(4).trim
    val activityId = rowContent.apply(5).trim
    val activityLabel = rowContent.apply(6).trim
    val competencyId= rowContent.apply(7).trim
    val competency = rowContent.apply(8).trim
    val competencyLevelId = rowContent.apply(9).trim
    listData.put("function",function)
    listData.put("year",year)
    listData.put("roleId",roleId)
    listData.put("roleLabel",roleLabel)
    listData.put("activityLabel",activityLabel)
    listData.put("competencyId",competencyId)
    listData.put("competency",competency)
    listData.put("competencyLevelId",competencyLevelId)
    fracclMap.put(competencyMapping.concat(activityId), listData)
    fracclMap
  }


  def getCompetenciesData(sheet: XSSFSheet): List[java.util.Map[String,util.HashMap[String, AnyRef]]]= {
    logger.info("enter into the getCompetenciesData")
    val column = sheet.asScala.drop(1).map(row =>
      if (sheet.getWorkbook.getSheetIndex(sheet) == 1 || sheet.getWorkbook.getSheetIndex(sheet) == 8)
        row.getCell(4)
      else
        row.getCell(5)
    )
    val formatter = new DataFormatter()
    val uniqueCompetencies = column.map(cell => formatter.formatCellValue(cell)).toList

    val rows = sheet.asScala.drop(1)
    getData = rows.flatMap(row => {
      val cell = if (sheet.getWorkbook.getSheetIndex(sheet) == 1 || sheet.getWorkbook.getSheetIndex(sheet) == 8)
        row.getCell(4)
      else
        row.getCell(5)

      // Add the following check for null, empty, and empty string:
      val cellValue = Option(cell).map(_.getStringCellValue.trim).getOrElse("")
      if (cellValue.nonEmpty) {
        if (uniqueCompetencies.contains(cellValue)) {
          Option(sheet.getRow(row.getRowNum)).map(parseCompetencyData)
        } else {
          None
        }
      } else {
        None
      }
    }).toList
    getData
  }


  def getCompetency(file: File): List[java.util.Map[String,AnyRef]]= {
    logger.info("enter into the getCompetency method")
    val finalData: mutable.Map[String, List[Map[String, AnyRef]]] = mutable.Map.empty
    try {
      val workbook = new XSSFWorkbook(new FileInputStream(file))
      (1 until workbook.getNumberOfSheets)
        .foreach(index => {
          if(index==1)
            getData = getCompetenciesData(workbook.getSheetAt(index))

          val convertedData = getData.map(_.asScala.toMap)
          finalData += (workbook.getSheetName(index) -> convertedData)
          getFinalData = finalData.toList.flatMap { case (_, maps) => maps.map(convertMap) }
        })
      getFinalData

    } catch {
      case e: Exception => throw new Exception("Invalid File")
    }
  }


  def convertMap(map: Map[String, AnyRef]): util.Map[String, AnyRef] = {
    val javaMap = new util.HashMap[String, AnyRef]()
    map.foreach { case (k, v) => javaMap.put(k, v) }
    javaMap
  }




}
