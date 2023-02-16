package org.sunbird.cloudstore

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.{BlobId, BlobInfo, HttpMethod, Storage, StorageOptions}
import org.apache.commons.lang3.StringUtils
import org.apache.tika.Tika
import org.apache.tika.metadata.HttpHeaders
import org.apache.tika.mime.MimeTypes
import org.sunbird.cloud.storage.BaseStorageService
import org.sunbird.cloud.storage.factory.{GcloudConfig, StorageConfig, StorageServiceFactory}
import org.sunbird.common.exception.ServerException
import org.sunbird.common.{Platform, Slug}

import java.io.File
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future};


class StorageService {

    val storageType: String = if (Platform.config.hasPath("cloud_storage_type")) Platform.config.getString("cloud_storage_type") else ""
    var storageService: BaseStorageService = null

    @throws[Exception]
    def getService: BaseStorageService = {
        if (null == storageService) {
            if (StringUtils.equalsIgnoreCase(storageType, "azure")) {
                val storageKey = Platform.config.getString("azure_storage_key")
                val storageSecret = Platform.config.getString("azure_storage_secret")
                storageService = StorageServiceFactory.getStorageService(StorageConfig(storageType, storageKey, storageSecret))
            } else if (StringUtils.equalsIgnoreCase(storageType, "aws")) {
                val storageKey = Platform.config.getString("aws_storage_key")
                val storageSecret = Platform.config.getString("aws_storage_secret")
                storageService = StorageServiceFactory.getStorageService(StorageConfig(storageType, storageKey, storageSecret))
            } else if (StringUtils.equalsIgnoreCase(storageType, "gcloud")) {
                val storageKey = Platform.config.getString("gcloud_client_key")
                val storageSecret = Platform.config.getString("gcloud_private_secret")
                val clientId = Platform.config.getString("gcloud_private_bucket_project_client_id")
                val clientEmail = Platform.config.getString("gcloud_client_key")
                val privateKeyPkcs8 = Platform.config.getString("gcloud_private_secret")
                val privateKeyIds = Platform.config.getString("gcloud_private_bucket_project_key_id")
                val projectId = Platform.config.getString("gcloud_private_bucket_projectId")
                storageService = StorageServiceFactory.getStorageService(StorageConfig(storageType, storageKey, storageSecret, Option.empty,
                  Option.apply(GcloudConfig(clientId, clientEmail, privateKeyPkcs8, privateKeyIds, projectId))))
            }
//            else if (StringUtils.equalsIgnoreCase(storageType, "cephs3")) {
//                val storageKey = Platform.config.getString("cephs3_storage_key")
//                val storageSecret = Platform.config.getString("cephs3_storage_secret")
//                val endpoint = Platform.config.getString("cephs3_storage_endpoint")
//                storageService = StorageServiceFactory.getStorageService(new StorageConfig(storageType, storageKey, storageSecret, Option(endpoint)))
//            }
            else throw new ServerException("ERR_INVALID_CLOUD_STORAGE", "Error while initialising cloud storage")
        }
        storageService
    }

    def getContainerName: String = {
      storageType match {
        case "azure" => Platform.config.getString("azure_storage_container")
        case "aws" => Platform.config.getString("aws_storage_container")
        case "gcloud" => Platform.config.getString("gcloud_storage_bucket")
        case _ => throw new ServerException("ERR_INVALID_CLOUD_STORAGE", "Container name not configured.")
      }
    }

    def uploadFile(folderName: String, file: File, slug: Option[Boolean] = Option(true)): Array[String] = {
        val slugFile = if (slug.getOrElse(true)) Slug.createSlugFile(file) else file
        val objectKey = folderName + "/" + slugFile.getName
        val url = getService.upload(getContainerName, slugFile.getAbsolutePath, objectKey, Option.apply(false), Option.apply(1), Option.apply(5), Option.empty)
        Array[String](objectKey, url)
    }

    def uploadDirectory(folderName: String, directory: File, slug: Option[Boolean] = Option(true)): Array[String] = {
        val slugFile = if (slug.getOrElse(true)) Slug.createSlugFile(directory) else directory
        val objectKey = folderName + File.separator
        val url = getService.upload(getContainerName, slugFile.getAbsolutePath, objectKey, Option.apply(true), Option.apply(1), Option.apply(5), Option.empty)
        Array[String](objectKey, url)
    }

    def uploadDirectoryAsync(folderName: String, directory: File, slug: Option[Boolean] = Option(true))(implicit ec: ExecutionContext): Future[List[String]] = {
        val slugFile = if (slug.getOrElse(true)) Slug.createSlugFile(directory) else directory
        val objectKey = folderName + File.separator
        getService.uploadFolder(getContainerName, slugFile.getAbsolutePath, objectKey, Option.apply(false), None, None, 1)
    }

    def getObjectSize(key: String): Double = {
        val blob = getService.getObject(getContainerName, key, Option.apply(false))
        blob.contentLength
    }

    def copyObjectsByPrefix(source: String, destination: String): Unit = {
        getService.copyObjects(getContainerName, source, getContainerName, destination, Option.apply(true))
    }

    def deleteFile(key: String, isDirectory: Option[Boolean] = Option(false)): Unit = {
        getService.deleteObject(getContainerName, key, isDirectory)
    }

    def getSignedURL(key: String, ttl: Option[Int], permission: Option[String]): String = {
      storageType match {
        //case "gcloud" => getGCPSignedURL(key, ttl.get)
        case "gcloud" => getService.getPutSignedURL(getContainerName, key, ttl, permission, Option.apply(getMimeType(key)))
        case _ => getService.getSignedURL (getContainerName, key, ttl, permission)
      }
    }

  def getGCPSignedURL(objectName: String, ttl: Long):  String = {
    val clientId = Platform.config.getString("gcloud_private_bucket_project_client_id")
    val clientEmail = Platform.config.getString("gcloud_client_key")
    val privateKeyPkcs8 = Platform.config.getString("gcloud_private_secret")
    val privateKeyIds = Platform.config.getString("gcloud_private_bucket_project_key_id")
    val projectId = Platform.config.getString("gcloud_private_bucket_projectId")
    println("uploading file to clientId : ", clientId)
    println("uploading file to clientEmail : ", clientEmail)
    println("uploading file to privateKeyPkcs8 : ", privateKeyPkcs8)
    println("uploading file to privateKeyIds : ", privateKeyIds)
    println("uploading file to projectId : ", projectId)
    println("uploading file to container : ", getContainerName)
    println("file name getting uploaded : ", objectName)
    val credentials = ServiceAccountCredentials.fromPkcs8(clientId, clientEmail, privateKeyPkcs8, privateKeyIds, new java.util.ArrayList[String]())
    println("credentials created")
    val storage = StorageOptions.newBuilder.setProjectId(projectId).setCredentials(credentials).build.getService
    println("storage object created")
    val extensionHeaders = new java.util.HashMap().asInstanceOf[java.util.Map[String, String]]
    extensionHeaders.putAll(Map(HttpHeaders.CONTENT_TYPE -> MimeTypes.OCTET_STREAM).asJava)
    //extensionHeaders.putAll(Map("x-goog-resumable" -> "start").asJava)
    println("*** --- Changed code --- *******")
    val blobInfo = BlobInfo.newBuilder(BlobId.of(getContainerName, objectName)).build
    println("blob object created")
    val expiryTime = if(ttl > 7) 7 else ttl
    println("expiry time : ", expiryTime)
    val url = storage.signUrl(blobInfo, expiryTime, TimeUnit.DAYS, Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
      Storage.SignUrlOption.withExtHeaders(extensionHeaders),
      Storage.SignUrlOption.withV4Signature);
    println("************* url created: ", url)
    url.toString;
  }

    def getUri(key: String): String = {
        try {
           getService.getUri(getContainerName, key, Option.apply(false))
        } catch {
            case e:Exception =>
              println("StorageService --> getUri --> Exception: " + e.getMessage)
              ""
        }
    }

  def getMimeType(fileName: String): String = {
    val tika: Tika = new Tika()
    tika.detect(fileName)
  }

  def main(args: Array[String]): Unit = {
    val storageConfig = StorageServiceFactory.getStorageService(StorageConfig("google", "jenkins@upsmf-368011.iam.gserviceaccount.com",
      "-----BEGINPRIVATEKEY-----MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCqtqMByEGjddwE0oIkQRT4KukhPn65ozDQUfgop55VblUWeJEqmeGfTXdOTTVHpwwuYR9esrMgR5WN8IUGSLRmap9iyb4QBUV/gCjJIpsVu6HFMadBQCFceqzTqPMK6g6dwObNtDMxH6yPV47L/McwiPNoug2W+zBiRQ6YZ1GvQVY5s0KTX6EgkN/u3DW6kUu6NqcgqGuCWqUoEjss4HaX4D7DSbmKgOts/rKjwtDv9fgKLgbMlufwpxwWe/jygVUNvZumBARNIuVe+RbO6OvHb26H18KgkdDzB1VkzKX+750iIIa/KGrZHJStiw0zfri0/H0KdzbClvoOT6cBN/zVAgMBAAECggEAPN9dJsCKr0fSiaGqqFTQrmFWufT36SgIuu/PpmeGrP3nt1iMov02F8DevvwG+KLXVB5rui/MccE00eUm+gOo2DBC304CyOGoU3uWj4oYdNpwJ8m50ymT+87+m4bOC2FEjvn/EvCjpGuf84kMMS7UtDjRWtGlEOZG7XOkbbHBzdTQGldzEgsqi2T8O5I31xZ1b2LJzAVODrv7TiVElhGcUB/1MkProjhkcyJx3B3cpClwY8Lq2R2urTf4NxMnmh/PmUfBzLQLauSDI/MH9NN57J1M/5uWYAIY/eaf8BtqEsbrXLmBP1WfNchXbfXLeadaiAX45ukt0y103qd0TmJa7QKBgQDdvgTcjKMddzfU6PeBXO3upl2FVLA6B89J/QiEKoeg4bkM2C3pNkGvgVA3HfHfauMhhwFee8mP14HLZQvb+0k9tL64CiznkuEfOBApkXJDsW0iAN3TwMj5zVRAVHWBRcexMt74CdySuKDOkV9G5feOXfdhOZM6z8LSfGs+2lYbQwKBgQDFFmj8Mtv4Pv5zsF1/UeFnuijkHO84dNYnflTB5Pmwb4Z5rhnJzG446cxr9f7E/+3yjd+xtBQf5ttPwvCBbZR20RO2jA5o/qijXaYHCjlE7yOpAfgU+p5K3JH9bTMLuPsSVaxBof7cFoqjFalVGmpR1qAj4UGHc9mTnV6CGCbqBwKBgQCTI+RV9XzHsLR7s5uJXAEGu56TOv81grkqf52HFjGpsB77RvgwKLCtpUF1UYmOl37gYJWn/Lxjlr2qGgMkljqjl6x2s0nY4L5B2RHgg4MvKC0iwzBvsx2ppXaiuWi/v24jR35tWR3kvl72s8Bla3Q6JGBjQ7FO9U5yHd2Md5VrwQKBgAzyQOk4KgzvjmVpE2s2pLjcmK0LXYd23U5w1P57nQ9C9DFwB0+jNyZT7VK2MQsdyLKjMSuKKbxCvOtLYeMOoK8BYusd3iB1gfxhPXO+7y4hC1WhxHsUT2uZe5mLH8xIVW3J5OvWyVgJvwehd6MYfh1sHM7ekCBmsscokjm3fm7nAoGBAL5PXhD6rCaHGOo0KXEA0S6rzMI6qBzQvMyOVj7b0lwey6q+G2xl7Cc9IUmxVzhBe7daD6QSQ4dU91ZKaIysopfZWibHFcQm6I6FJI3ha73EOB2zyyl3xlBxK9fMQVN8gELdXhA8DBuMD+Qxj6NrbqteFJLCyz7ATtETSb3+hP+G-----ENDPRIVATEKEY-----",
      None, Option.apply(GcloudConfig("113740098487205958998",
        "jenkins@upsmf-368011.iam.gserviceaccount.com",
        "-----BEGINPRIVATEKEY-----MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCqtqMByEGjddwE0oIkQRT4KukhPn65ozDQUfgop55VblUWeJEqmeGfTXdOTTVHpwwuYR9esrMgR5WN8IUGSLRmap9iyb4QBUV/gCjJIpsVu6HFMadBQCFceqzTqPMK6g6dwObNtDMxH6yPV47L/McwiPNoug2W+zBiRQ6YZ1GvQVY5s0KTX6EgkN/u3DW6kUu6NqcgqGuCWqUoEjss4HaX4D7DSbmKgOts/rKjwtDv9fgKLgbMlufwpxwWe/jygVUNvZumBARNIuVe+RbO6OvHb26H18KgkdDzB1VkzKX+750iIIa/KGrZHJStiw0zfri0/H0KdzbClvoOT6cBN/zVAgMBAAECggEAPN9dJsCKr0fSiaGqqFTQrmFWufT36SgIuu/PpmeGrP3nt1iMov02F8DevvwG+KLXVB5rui/MccE00eUm+gOo2DBC304CyOGoU3uWj4oYdNpwJ8m50ymT+87+m4bOC2FEjvn/EvCjpGuf84kMMS7UtDjRWtGlEOZG7XOkbbHBzdTQGldzEgsqi2T8O5I31xZ1b2LJzAVODrv7TiVElhGcUB/1MkProjhkcyJx3B3cpClwY8Lq2R2urTf4NxMnmh/PmUfBzLQLauSDI/MH9NN57J1M/5uWYAIY/eaf8BtqEsbrXLmBP1WfNchXbfXLeadaiAX45ukt0y103qd0TmJa7QKBgQDdvgTcjKMddzfU6PeBXO3upl2FVLA6B89J/QiEKoeg4bkM2C3pNkGvgVA3HfHfauMhhwFee8mP14HLZQvb+0k9tL64CiznkuEfOBApkXJDsW0iAN3TwMj5zVRAVHWBRcexMt74CdySuKDOkV9G5feOXfdhOZM6z8LSfGs+2lYbQwKBgQDFFmj8Mtv4Pv5zsF1/UeFnuijkHO84dNYnflTB5Pmwb4Z5rhnJzG446cxr9f7E/+3yjd+xtBQf5ttPwvCBbZR20RO2jA5o/qijXaYHCjlE7yOpAfgU+p5K3JH9bTMLuPsSVaxBof7cFoqjFalVGmpR1qAj4UGHc9mTnV6CGCbqBwKBgQCTI+RV9XzHsLR7s5uJXAEGu56TOv81grkqf52HFjGpsB77RvgwKLCtpUF1UYmOl37gYJWn/Lxjlr2qGgMkljqjl6x2s0nY4L5B2RHgg4MvKC0iwzBvsx2ppXaiuWi/v24jR35tWR3kvl72s8Bla3Q6JGBjQ7FO9U5yHd2Md5VrwQKBgAzyQOk4KgzvjmVpE2s2pLjcmK0LXYd23U5w1P57nQ9C9DFwB0+jNyZT7VK2MQsdyLKjMSuKKbxCvOtLYeMOoK8BYusd3iB1gfxhPXO+7y4hC1WhxHsUT2uZe5mLH8xIVW3J5OvWyVgJvwehd6MYfh1sHM7ekCBmsscokjm3fm7nAoGBAL5PXhD6rCaHGOo0KXEA0S6rzMI6qBzQvMyOVj7b0lwey6q+G2xl7Cc9IUmxVzhBe7daD6QSQ4dU91ZKaIysopfZWibHFcQm6I6FJI3ha73EOB2zyyl3xlBxK9fMQVN8gELdXhA8DBuMD+Qxj6NrbqteFJLCyz7ATtETSb3+hP+G-----ENDPRIVATEKEY-----",
        "6aef3a75efe29225e6347244de3e8f1ddd8437df","upsmf-368011"))))
    println("storage config : "+ storageConfig)
    val str = storageConfig.getPutSignedURL("dev-public-upsmf", "sample.txt", Option.apply(7), None, Option.apply("application/octet-stream"))
    println(str)

  }

}
