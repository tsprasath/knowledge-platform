package org.sunbird.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.sunbird.common.dto.Request
import org.sunbird.common.Platform

import java.nio.charset.StandardCharsets
import java.util.{Base64, Date}

object JwtGenerator {

  val secret: String = Platform.getString("secret.key","")

  def generateToken(request: Request, childIds: Seq[String]): String = {

    val now = new Date()

    val jwtBuilder = JWT.create()
      .withIssuedAt(now)
      .withClaim(Constants.CONTENTID, request.get(Constants.ROOTID).asInstanceOf[String])
      .withClaim(Constants.COLLECTIONID, request.get(Constants.COLLECTIONID).asInstanceOf[String])
      .withClaim(Constants.USERID, request.get(Constants.USERID).asInstanceOf[String])
      .withClaim(Constants.ATTEMPTID, request.get(Constants.ATTEMPTID).asInstanceOf[String])
      .withArrayClaim(Constants.QUESTIONLIST, childIds.toArray)
    jwtBuilder.sign(Algorithm.HMAC256(secret))

  }

  def decodeToken(token: String): String = {
    try {
      val algorithm = Algorithm.HMAC256(secret)
      val verifier = JWT.require(algorithm).build()
      verifier.verify(token)
      val payload = JWT.decode(token).getPayload
      println("printing payload",payload)
      val data = new String(Base64.getUrlDecoder.decode(payload),StandardCharsets.UTF_8)
      println("printing data",data)
      data
    } catch {
      case ex: Exception => {
        println(s"Error decoding JWT token: ${ex.getMessage}")
        throw ex
      }
    }
  }
}
