package com.imdb.service

import com.imdb.models.dao._
import com.imdb.models.domain.{Film, PersonInfo, Product}

trait SearchServiceUtil {

  protected def convertToPersonInfo(person: NameBasicDAO, role: TitlePrincipalDAO): PersonInfo =
    PersonInfo(
      role.tconst,
      person.nconst,
      person.primaryName,
      person.birthYear,
      role.job.getOrElse("Undefined")
    )

  protected def convertToFilm(titleBasic: TitleBasicDAO, rating: TitleRatingDAO): Film =
    Film(
      titleBasic.tconst,
      titleBasic.primaryTitle,
      titleBasic.originalTitle,
      titleBasic.startYear,
      rating.averageRating,
      titleBasic.genres
    )

  protected def convertToFilmInfo(films: Seq[Film], cast: Seq[PersonInfo]): Seq[Product] = {
    val filmPersonMap = cast.groupBy(_.tconst)

    films.map { film =>
      Product(
        id = film.filmTconst,
        title = film.filmPrimaryTitle,
        originalTitle = film.filmOriginalTitle,
        year = film.filmStartYear,
        rating = film.averageRating,
        genre = film.genre,
        castsCrews = filmPersonMap.getOrElse(film.filmTconst, Seq.empty)
      )
    }
  }

  protected def convertFilmDataToEntity(films: Seq[(TitleRatingDAO, TitleBasicDAO)]): Seq[Film] =
    films.map { case (rating, film) => convertToFilm(film, rating) }

  protected def convertCastDataToEntity(participants: Seq[(TitlePrincipalDAO, NameBasicDAO)]): Seq[PersonInfo] =
    participants.map {
      case (role, info) => convertToPersonInfo(info, role)
    }

}
