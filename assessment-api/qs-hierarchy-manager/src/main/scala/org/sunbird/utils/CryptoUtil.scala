package org.sunbird.utils

import java.nio.charset.StandardCharsets
import java.security._
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac
import scala.util.{Try, Either, Left, Right}
object CryptoUtil {
  private val US_ASCII = StandardCharsets.US_ASCII

//  def generateHMAC(payLoad: String, secretKey: String, algorithm: String): Array[Byte] = {
//    try {
//      val mac = Mac.getInstance(algorithm)
//      mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm))
//      mac.doFinal(payLoad.getBytes(US_ASCII))
//    } catch {
//      case _: NoSuchAlgorithmException | _: InvalidKeyException => null
//    }
//  }

  def generateRSASign(payLoad: String, key: PrivateKey, algorithm: String): Array[Byte] = {
    val sign: Signature = Signature.getInstance(algorithm)
    sign.initSign(key)
    sign.update(payLoad.getBytes(StandardCharsets.US_ASCII))
    sign.sign()
  }

  def verifyRSASign(payLoad: String, signature: Array[Byte], key: PublicKey, algorithm: String): Boolean = {
    Try {
      val sign = Signature.getInstance(algorithm)
      sign.initVerify(key)
      sign.update(payLoad.getBytes(StandardCharsets.US_ASCII))
      sign.verify(signature)
    }.getOrElse(false)
  }
}
