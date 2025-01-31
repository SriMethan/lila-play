package lila.swiss

import strategygames.{ Player => PlayerIndex, GameLogic }
import strategygames.format.FEN
import reactivemongo.api.bson._
import scala.concurrent.duration._

import lila.db.BSON
import lila.db.dsl._
import lila.user.User

object BsonHandlers {

  implicit val variantHandler       = variantByKeyHandler
  implicit val stratVariantHandler  = stratVariantByKeyHandler
  implicit val clockHandler         = clockConfigHandler
  implicit val swissPointsHandler   = intAnyValHandler[Swiss.Points](_.double, Swiss.Points.apply)
  implicit val swissTieBreakHandler = doubleAnyValHandler[Swiss.TieBreak](_.value, Swiss.TieBreak.apply)
  implicit val swissPerformanceHandler =
    floatAnyValHandler[Swiss.Performance](_.value, Swiss.Performance.apply)
  implicit val swissScoreHandler  = intAnyValHandler[Swiss.Score](_.value, Swiss.Score.apply)
  implicit val roundNumberHandler = intAnyValHandler[SwissRound.Number](_.value, SwissRound.Number.apply)
  implicit val swissIdHandler     = stringAnyValHandler[Swiss.Id](_.value, Swiss.Id.apply)
  implicit val playerIdHandler    = stringAnyValHandler[SwissPlayer.Id](_.value, SwissPlayer.Id.apply)

  implicit val playerHandler = new BSON[SwissPlayer] {
    import SwissPlayer.Fields._
    def reads(r: BSON.Reader) =
      SwissPlayer(
        id = r.get[SwissPlayer.Id](id),
        swissId = r.get[Swiss.Id](swissId),
        userId = r str userId,
        rating = r int rating,
        provisional = r boolD provisional,
        points = r.get[Swiss.Points](points),
        tieBreak = r.get[Swiss.TieBreak](tieBreak),
        performance = r.getO[Swiss.Performance](performance),
        score = r.get[Swiss.Score](score),
        absent = r.boolD(absent),
        byes = ~r.getO[Set[SwissRound.Number]](byes)
      )
    def writes(w: BSON.Writer, o: SwissPlayer) =
      $doc(
        id          -> o.id,
        swissId     -> o.swissId,
        userId      -> o.userId,
        rating      -> o.rating,
        provisional -> w.boolO(o.provisional),
        points      -> o.points,
        tieBreak    -> o.tieBreak,
        performance -> o.performance,
        score       -> o.score,
        absent      -> w.boolO(o.absent),
        byes        -> o.byes.some.filter(_.nonEmpty)
      )
  }

  implicit val pairingStatusHandler = lila.db.dsl.quickHandler[SwissPairing.Status](
    {
      case BSONBoolean(true)  => Left(SwissPairing.Ongoing)
      case BSONInteger(index) => Right(PlayerIndex.fromP1(index == 0).some)
      case _                  => Right(none)
    },
    {
      case Left(_)        => BSONBoolean(true)
      case Right(Some(c)) => BSONInteger(c.fold(0, 1))
      case _              => BSONNull
    }
  )
  implicit val pairingHandler = new BSON[SwissPairing] {
    import SwissPairing.Fields._
    def reads(r: BSON.Reader) =
      r.get[List[User.ID]](players) match {
        case List(w, b) =>
          SwissPairing(
            id = r str id,
            swissId = r.get[Swiss.Id](swissId),
            round = r.get[SwissRound.Number](round),
            p1 = w,
            p2 = b,
            status = r.getO[SwissPairing.Status](status) | Right(none),
            // TODO: long term we may want to skip storing both of these fields
            //       in the case that it's not a micromatch to save on storage
            isMicroMatch = r.getD[Boolean](isMicroMatch),
            microMatchGameId = r.getO[String](microMatchGameId),
            //TODO allow this to work for chess too?
            openingFEN = r.getO[String](openingFEN).map(fen => FEN(GameLogic.Draughts(), fen))
          )
        case _ => sys error "Invalid swiss pairing users"
      }
    def writes(w: BSON.Writer, o: SwissPairing) =
      $doc(
        id      -> o.id,
        swissId -> o.swissId,
        round   -> o.round,
        players -> o.players,
        status  -> o.status,
        // TODO: long term we may want to skip storing both of these fields
        //       in the case that it's not a micromatch to save on storage
        isMicroMatch     -> o.isMicroMatch,
        microMatchGameId -> o.microMatchGameId,
        openingFEN       -> o.openingFEN.map(_.value)
      )
  }
  implicit val pairingGamesHandler = new BSON[SwissPairingGameIds] {
    import SwissPairing.Fields._
    def reads(r: BSON.Reader) =
      SwissPairingGameIds(
        id = r str id,
        isMicroMatch = r.get[Boolean](isMicroMatch),
        microMatchGameId = r.getO[String](microMatchGameId),
        //TODO allow this to work for chess too?
        openingFEN = r.getO[String](openingFEN).map(fen => FEN(GameLogic.Draughts(), fen))
      )
    def writes(w: BSON.Writer, o: SwissPairingGameIds) =
      $doc(
        id               -> o.id,
        isMicroMatch     -> o.isMicroMatch,
        microMatchGameId -> o.microMatchGameId,
        openingFEN       -> o.openingFEN.map(_.value)
      )
  }

  import SwissCondition.BSONHandlers.AllBSONHandler

  implicit val settingsHandler = new BSON[Swiss.Settings] {
    def reads(r: BSON.Reader) =
      Swiss.Settings(
        nbRounds = r.get[Int]("n"),
        rated = r.boolO("r") | true,
        isMicroMatch = r.boolO("m") | false,
        description = r.strO("d"),
        useDrawTables = r.boolO("dt") | false,
        position = r.getO[FEN]("f"),
        chatFor = r.intO("c") | Swiss.ChatFor.default,
        roundInterval = (r.intO("i") | 60).seconds,
        password = r.strO("p"),
        conditions = r.getO[SwissCondition.All]("o") getOrElse SwissCondition.All.empty,
        forbiddenPairings = r.getD[String]("fp")
      )
    def writes(w: BSON.Writer, s: Swiss.Settings) =
      $doc(
        "n"  -> s.nbRounds,
        "r"  -> (!s.rated).option(false),
        "m"  -> s.isMicroMatch,
        "d"  -> s.description,
        "dt" -> s.useDrawTables,
        "f"  -> s.position,
        "c"  -> (s.chatFor != Swiss.ChatFor.default).option(s.chatFor),
        "i"  -> s.roundInterval.toSeconds.toInt,
        "p"  -> s.password,
        "o"  -> s.conditions.ifNonEmpty,
        "fp" -> s.forbiddenPairings.some.filter(_.nonEmpty)
      )
  }

  implicit val swissHandler = Macros.handler[Swiss]

  // "featurable" mostly means that the tournament isn't over yet
  def addFeaturable(s: Swiss) =
    swissHandler.writeTry(s).get ++ {
      s.isNotFinished ?? $doc(
        "featurable" -> true,
        "garbage"    -> s.unrealisticSettings.option(true)
      )
    }

  import Swiss.IdName
  implicit val SwissIdNameBSONHandler = Macros.handler[IdName]
}
