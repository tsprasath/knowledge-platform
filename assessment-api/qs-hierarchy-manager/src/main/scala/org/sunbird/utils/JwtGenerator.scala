package org.sunbird.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.sunbird.common.dto.Request
import org.sunbird.common.Platform

import java.nio.charset.StandardCharsets
import java.util.{Base64, Date}

object JwtGenerator {

  val secret: String = Platform.getString("secret.key","")

  def jwt(in: Map[String, String], inArray: Map[String, Array[String]], timeStamp: Date = new Date()): String = {
     val jwtBuilder = JWT.create().withIssuedAt(timeStamp)
     in.foreach(i => jwtBuilder.withClaim(i._1, i._2))
     inArray.foreach(i => jwtBuilder.withArrayClaim(i._1, i._2))
    jwtBuilder.sign(Algorithm.HMAC256(secret))
  }

  def jwtPayload(token: String): String = {
    JWT.require(Algorithm.HMAC256(secret)).build().verify(token)
    new String(Base64.getUrlDecoder.decode(JWT.decode(token).getPayload), StandardCharsets.UTF_8)
  }

}
