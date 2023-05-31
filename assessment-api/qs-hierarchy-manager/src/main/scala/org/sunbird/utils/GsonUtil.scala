package org.sunbird.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


import java.lang.reflect.Type
import java.util.Map

object GsonUtil {
  private var sGson: Gson = _

  def getGson: Gson = {
    if (sGson == null) {
      sGson = new GsonBuilder().setLenient().create()
    }
    sGson
  }

  def fromJson[C](json: String, classOfC: Class[C]): C = {
    getGson.fromJson(json, classOfC)
  }

  def fromJson[T](json: String, `type`: Type): T = {
    getGson.fromJson(json, `type`)
  }

  def fromJson[T](json: String, classOfT: Class[T], exceptionMessage: String): T = {
    getGson.fromJson(json, classOfT)
  }

  def fromMap[C](map: Map[_, _], classOfC: Class[C]): C = {
    val jsonElement = getGson.toJsonTree(map)
    getGson.fromJson(jsonElement, classOfC)
  }

  def toJson(json: Any): String = {
    getGson.toJson(json)
  }
}