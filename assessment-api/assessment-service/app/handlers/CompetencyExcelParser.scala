package handlers

import handlers.CompetencyExcelParser.fracclMap
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.{XSSFRow, XSSFSheet, XSSFWorkbook}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{File, FileInputStream}
import java.util
import scala.collection.JavaConverters.{asScalaIteratorConverter, iterableAsScalaIterableConverter, mapAsScalaMapConverter}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.sys.SystemProperties.headless.key

case class Activity(code: String, label: String)

object CompetencyExcelParser {

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  private var getData: List[util.Map[String,AnyRef]] = List.empty

  private var getFinalData: List[java.util.Map[String,AnyRef]] = List.empty

  private var listData :util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]
  private var fracclMap: mutable.Map[String, AnyRef] = mutable.Map.empty
  private var competencyMap: util.Map[String, AnyRef] = new util.HashMap[String,AnyRef]()
  var dataList: List[util.Map[String, AnyRef]] = List.empty
  var data: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
  var map: List[util.Map[String, AnyRef]] = List.empty
  var dataMap: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
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
    competencyMap.put(competencyId, competency)
    listData.put("compefracclMaptency",competency)
    listData.put("competencyLevelId",competencyLevelId)
    fracclMap.put(competencyMapping.concat(activityId), listData)
    fetchDataFromFracclMap(competencyMapping.concat(activityId), fracclMap)
   // fetchsheetdata(listData)
   //listData
  }


  def getCompetenciesData(sheet: XSSFSheet)= {
    logger.info("enter into the getCompetenciesData")
    val column = sheet.asScala.drop(1).map(row =>
      if (sheet.getWorkbook.getSheetIndex(sheet) == 1 || sheet.getWorkbook.getSheetIndex(sheet) == 8){
        row.getCell(4)
      } else
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
      //getData = getCompetenciesData(workbook.getSheetAt(1))
      (1 until workbook.getNumberOfSheets)
        .foreach(index => {

            getData = getCompetenciesData(workbook.getSheetAt(index))

          getSheetBasedOnRoleLabel(getData,workbook.getSheetAt(index))

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

  def fetchDataFromFracclMap(competencylevel:String,fracclMap:mutable.Map[String, AnyRef]):util.Map[String, AnyRef]= {
    logger.info("Enter into the fetchDataFromFracclMap")

    val competencyLevelValue= fracclMap.get(competencylevel)
    val key:String="roleLabel"
    val levelValue: AnyRef = competencyLevelValue match {
      case Some(x) => {
        val dataLabel = x.asInstanceOf[util.Map[String, AnyRef]]
        var valueOption: AnyRef = dataLabel.get(key)
        valueOption
      }
        case None => "None"


      }
    val activity: AnyRef = /*levelValue match{
      case levelValue=>*/
        competencyLevelValue match {
          case Some(x) => {
            val dataLabel = x.asInstanceOf[util.Map[String, AnyRef]]
            var valueOption: AnyRef = dataLabel.get("activityLabel")
            valueOption
          }
          case None => "None"


    }

      data.put(competencylevel, activity)

    //dataList=dataList:+data

    dataMap.put(levelValue.toString,data)
    dataMap

    }

  def getSheetBasedOnRoleLabel(data:List[util.Map[String, AnyRef]], sheet: XSSFSheet):util.Map[String,AnyRef]={
    logger.info("Enter into the getSheetBasedOnRoleLabel")
    val sheetMap:util.Map[String,AnyRef]=new util.HashMap[String,AnyRef]()
    val keyValue = data.flatMap(m => m.keySet().asScala).distinct.toList
    val value = data.flatMap(n=> n.values().asScala).distinct
    val activity:String=null
    /*value.foreach{v=>
      activity= v match{
        case Some(x)=>{
        val activityLabel:String=x.asInstanceOf
      }
      }
    }*/
   // val innerValue = data.flatMap(_.get(key)).map(_.innerValue).getOrElse(Seq.empty)
    keyValue.foreach(k=>
      if(k.equals(sheet.getSheetName) ){


      } else {

      }
    )

    sheetMap.put("key","value").asInstanceOf[util.Map[String,AnyRef]]
    }




  }
