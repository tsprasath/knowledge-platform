package org.sunbird.utils

import org.slf4j.LoggerFactory

import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import scala.collection.mutable.HashMap
import java.util.Base64

class KeyManager(private val basePath: String, private val keyPrefix: String, private val keyCount: Int) {
  private val log = LoggerFactory.getLogger(this.getClass)

  private val keyMap: HashMap[String, KeyData] = new HashMap[String, KeyData]()

  init()


  private def init(): Unit = {
    loadKeys()
  }

  private def loadKeys(): Unit = {
    for (i <- 0 until keyCount) {
      val keyId = keyPrefix + i
      log.info("Private key loaded - " + basePath + keyId)
      keyMap.put(keyId, new KeyData(keyId, getPrivateKey(basePath + keyId + ".pem"), loadPublicKey(basePath + keyId + ".pub")))
    }
  }

  private def loadPublicKey(path: String): PublicKey = {
    val in = new FileInputStream(path)
    val keyBytes = new Array[Byte](in.available())
    in.read(keyBytes)
    in.close()

    val publicKey = new String(keyBytes, StandardCharsets.UTF_8)
      .replaceAll("(-+BEGIN RSA PUBLIC KEY-+\\r?\\n|-+END RSA PUBLIC KEY-+\\r?\\n?)", "")
      .replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "")
//      .replaceAll("-", "")
      .replaceAll("\\s", "")

    val publicBytes = Base64.getDecoder.decode(publicKey)
    val keySpec = new X509EncodedKeySpec(publicBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    log.info("Public key loaded - " + path)
    keyFactory.generatePublic(keySpec)
  }

  def getRandomKey(): KeyData = {
    val keyId = keyPrefix + (Math.random() * keyCount).toInt
    keyMap.getOrElse(keyId, null)
  }

  private def getPrivateKey(path: String): PrivateKey = {
    val in = new FileInputStream(path)
    val keyBytes = new Array[Byte](in.available())
    in.read(keyBytes)
    in.close()

    var privateKey = new String(keyBytes, "UTF-8")
    privateKey = privateKey
      .replaceAll("(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "")
      .replaceAll("(-+BEGIN PRIVATE KEY-+\\r?\\n|-+END PRIVATE KEY-+\\r?\\n?)", "")

    val keySpec = new PKCS8EncodedKeySpec(Base64Util.decode(privateKey.getBytes("UTF-8"), Base64Util.DEFAULT))
    val keyFactory = KeyFactory.getInstance("RSA")
    keyFactory.generatePrivate(keySpec)
  }

  def getValueFromKeyMap(keyId: String): KeyData = {
    keyMap.getOrElse(keyId, throw new NoSuchElementException(s"KeyData not found for keyId: $keyId"))
  }

}
