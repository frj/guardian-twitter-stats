package com.gu.twitterstats

import com.gu.openplatform.contentapi.Api
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.model.Content
import com.gu.openplatform.contentapi.connection.ApacheHttpClient
import net.liftweb.json._
import java.net.URLEncoder
import au.com.bytecode.opencsv.CSVWriter
import java.io.{FileWriter, OutputStreamWriter}

object TwitterStats extends ApacheHttpClient {
  def main(args: Array[String]) = {
    val dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    def getResults(fromPage:Int, fromDate:DateTime, toDate:DateTime ):List[Content] = {
      Api.search.fromDate(fromDate).toDate(toDate).page(fromPage).pageSize(50) match {
        case response if response.currentPage < response.pages =>
          response.results ::: getResults(response.currentPage + 1, fromDate, toDate)
        case response => response.results
      }
    }

    case class TwitterResponse(count:Int, url:String)
    implicit val formats = DefaultFormats

    val results = getResults(1,dateTimeFormat.parseDateTime(args(0)), dateTimeFormat.parseDateTime(args(1))).flatMap { content =>
      GET("http://urls.api.twitter.com/1/urls/count.json?url=" + URLEncoder.encode(content.webUrl, "UTF-8")) match {
        case response if response.statusCode == 200 => List(JsonParser.parse(response.body).extract[TwitterResponse])
        case failedResponse =>
          print("Failed to get link count from twitter:" + failedResponse.statusCode + " " + failedResponse.statusMessage)
          Nil
      }
    }

    val writer  = new CSVWriter(new FileWriter(args(2)))
    results.foreach( result =>
      writer.writeNext(Array(result.url,result.count.toString))
    )
    writer.flush
    writer.close
  }
}