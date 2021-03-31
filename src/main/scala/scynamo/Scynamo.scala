package scynamo

import cats.data.EitherNec
import software.amazon.awssdk.services.dynamodb.model.{GetItemResponse, QueryResponse}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import cats.implicits._

object Scynamo extends ScynamoFunctions

trait ScynamoFunctions {
  def decodeGetItemResponse[A: ObjectScynamoDecoder](response: GetItemResponse): EitherNec[ScynamoDecodeError, Option[A]] =
    if (response.hasItem)
      ObjectScynamoDecoder[A].decodeMap(response.item()).map(Some(_))
    else
      Right(None)

  def decodeQueryResponse[A: ObjectScynamoDecoder](response: QueryResponse): Iterable[EitherNec[ScynamoDecodeError, A]] =
    if (response.hasItems)
      response.items().map(ObjectScynamoDecoder[A].decodeMap(_))
    else Seq.empty
}
