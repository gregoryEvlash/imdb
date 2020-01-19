package com.imdb.json

import com.imdb.models.domain._
import io.circe.{Encoder, Json}
import io.circe.generic
import io.circe.generic.semiauto._

package object response {

  implicit val personInfoEncoder: Encoder[PersonInfo] =  x =>
    Json.fromFields(
      Seq(
        "id" -> Json.fromString(x.nconst),
        "name" -> Json.fromString(x.personPrimaryName),
        "category" -> Json.fromString(x.category),
        "birth" ->Json.fromString(x.personBirthYear)
      )
    )

  implicit val productEncoder: Encoder[Product] = deriveEncoder[Product]

  implicit val filmEncoder: Encoder[Film] = deriveEncoder[Film]

  implicit val titleSearchResultEncoder: Encoder[TitleSearchResult] =
    deriveEncoder[TitleSearchResult]
  implicit lazy val genreSearchResultEncoder: Encoder[GenresSearchResult] =
    deriveEncoder[GenresSearchResult]
  implicit lazy val sixDegreesResultEncoder: Encoder[SixDegreesResult] =
    deriveEncoder[SixDegreesResult]

  implicit val personNotFountEncoder: Encoder[PersonNotFount] = deriveEncoder[PersonNotFount]

  implicit val personUnreachableEncoder: Encoder[PersonUnreachable] =  x =>
    Json.fromString(s"$x cant be reached less than 6 degree")

  implicit val customErrorEncoder: Encoder[CustomError] = deriveEncoder[CustomError]

  implicit val IMDBServiceErrorEncoder: Encoder[IMDBServiceError] = {
    case x: PersonNotFount    => personNotFountEncoder.apply(x)
    case x: PersonUnreachable => personUnreachableEncoder.apply(x)
    case x: CustomError       => customErrorEncoder.apply(x)
  }

  implicit val IMDBServiceResultEncoder: Encoder[IMDBServiceResult] = {
    case x: TitleSearchResult  => titleSearchResultEncoder.apply(x)
    case x: GenresSearchResult => genreSearchResultEncoder.apply(x)
    case x: SixDegreesResult   => sixDegreesResultEncoder.apply(x)
  }

}
