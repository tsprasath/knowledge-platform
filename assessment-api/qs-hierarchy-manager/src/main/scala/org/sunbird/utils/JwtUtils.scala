package org.sunbird.utils

import org.sunbird.managers.{KeyData, KeyManager}
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.util.{Base64, HashMap, Map}

object JwtUtils {

  private val SEPARATOR = "."

  def createRS256Token(key: String, privateKey: PrivateKey, headerOptions: Map[String, String]): String = {
    val tokenType = JWTokenType.RS256
    val payLoad = createHeader(tokenType, headerOptions) + SEPARATOR + createClaims(key)
    val signature = encodeToBase64Uri(CryptoUtil.generateRSASign(payLoad, privateKey, tokenType.algorithmName))
    payLoad + SEPARATOR + signature
  }


  private def createHeader(tokenType: JWTokenType, headerOptions: Map[String, String]): String = {
    val headerData = new HashMap[String, String]()
    if (headerOptions != null)
      headerData.putAll(headerOptions)
    headerData.put("alg", tokenType.tokenType)
    encodeToBase64Uri(GsonUtil.toJson(headerData).getBytes)
  }

  private def createClaims(subject: String): String = {
    val payloadData = new HashMap[String, Any]()
    payloadData.put("data", subject)
    payloadData.put("iat", System.currentTimeMillis / 1000)
    encodeToBase64Uri(GsonUtil.toJson(payloadData).getBytes)
  }

  private def encodeToBase64Uri(data: Array[Byte]): String = {
    Base64Util.encodeToString(data, 11)
  }

  def verifyRS256Token(token: String, keyManager: KeyManager, keyId: String): Boolean = {
    val tokenElements = token.split("\\.")
    val header = tokenElements(0)
    val body = tokenElements(1)
    val signature = tokenElements(2)
    val payLoad = header + SEPARATOR + body
    var keyData: KeyData = null
    var isValid = false
    keyData = keyManager.getValueFromKeyMap(keyId)
    if (keyData != null) {
      isValid = CryptoUtil.verifyRSASign(payLoad, decodeFromBase64(signature), keyData.getPublicKey, "SHA256withRSA")
    }
    isValid
  }

  def decodeFromBase64(data: String): Array[Byte] = {
    Base64Util.decode(data, 11)
  }

}
