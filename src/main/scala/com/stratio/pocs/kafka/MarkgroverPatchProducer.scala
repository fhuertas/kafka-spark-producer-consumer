package com.stratio.pocs.kafka

import java.util.Properties
import java.util.concurrent.ExecutionException

import scala.util._

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * https://github.com/apache/spark/pull/10953
  *
  */
object MarkgroverPatchProducer extends App {

  override def main(args: Array[String]): Unit = {
    val topic = "test"
    val isAsync = false

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
//    props.put("client.id", "DemoProducer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


    props.put("security.protocol", "SSL")
    props.put("ssl.protocol", "TLS")
    props.put("ssl.keystore.location", this.getClass.getResource("../../../../ssl/server.keystore.jks").getPath)
    props.put("ssl.keystore.password", "test1234")
    props.put("ssl.key.password", "test1234")
    props.put("ssl.truststore.location", this.getClass.getResource("../../../../ssl/server.truststore.jks")
      .getPath)
    props.put("ssl.truststore.password", "test1234")
    props.put("ssl.client.auth", "required")
    props.put("ssl.enabled.protocols", "TLSv1.2,TLSv1.1,TLSv1")
    props.put("ssl.keystore.type", "JKS")
    props.put("ssl.truststore.type", "JKS")
    val producer: KafkaProducer[Int, String] = new KafkaProducer(props)

    val messageNo: Int = 1
    val messageStr: String = "Message_1"
    val startTime = System.currentTimeMillis()
    if (isAsync) { // Send asynchronously
      producer.send(new ProducerRecord(topic,
        messageNo,
        messageStr))
    } else { // Send synchronously
      Try {
        producer.send(new ProducerRecord(topic,
          messageNo,
          messageStr)).get()
        System.out.println("Sent message: (" + messageNo + ", " + messageStr + ")")
      } match {
        case Success(_)  =>
        case Failure(e) => e.printStackTrace()
      }
    }
  }
}
