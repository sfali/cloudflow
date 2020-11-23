/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloudflow.spark

import cloudflow.streamlets.CodecInlet
import org.slf4j.LoggerFactory

// This data converter is used for both Akka Streams and Flink
// Spark does not support optional, so we need a separate implementation for Spark

abstract class SparkInletDataConverter[T] {
  protected var inlet: CodecInlet[T] = _

  def forInlet(in: CodecInlet[T]): Unit = inlet = in
  def convertData(data: Array[Byte]): T
}

case class DefaultSparkInletDataConverter[T]() extends SparkInletDataConverter[T] {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def convertData(data: Array[Byte]): T =
    try {
      inlet.codec.decode(data)
    } catch {
      case t: Throwable =>
        logger.error(s"Input data $data can not be transformed and will be skipped")
        logger.error(s"Data transformation error id ${t.getMessage}")
        null.asInstanceOf[T]
    }
}
