package lila.app
package templating

import strategygames.{ DisplayLib, Mode, Speed }
import strategygames.variant.Variant
import play.api.i18n.Lang

import lila.i18n.{ I18nKeys => trans }
import lila.pref.Pref
import lila.report.Reason
import lila.setup.TimeMode

trait SetupHelper { self: I18nHelper =>

  type SelectChoice = (String, String, Option[String])

  val clockTimeChoices: List[SelectChoice] = List(
    ("0", "0", none),
    ("0.25", "¼", none),
    ("0.5", "½", none),
    ("0.75", "¾", none)
  ) ::: List(
    "1",
    "1.5",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "10",
    "11",
    "12",
    "13",
    "14",
    "15",
    "16",
    "17",
    "18",
    "19",
    "20",
    "25",
    "30",
    "35",
    "40",
    "45",
    "60",
    "75",
    "90",
    "105",
    "120",
    "135",
    "150",
    "165",
    "180"
  ).map { v =>
    (v, v, none)
  }

  val clockIncrementChoices: List[SelectChoice] = {
    (0 to 20).toList ::: List(25, 30, 35, 40, 45, 60, 90, 120, 150, 180)
  } map { s =>
    (s.toString, s.toString, none)
  }

  val corresDaysChoices: List[SelectChoice] =
    ("1", "One day", none) :: List(2, 3, 5, 7, 10, 14).map { d =>
      (d.toString, s"$d days", none)
    }

  def translatedTimeModeChoices(implicit lang: Lang) =
    List(
      (TimeMode.RealTime.id.toString, trans.realTime.txt(), none),
      (TimeMode.Correspondence.id.toString, trans.correspondence.txt(), none),
      (TimeMode.Unlimited.id.toString, trans.unlimited.txt(), none)
    )

  def translatedReasonChoices(implicit lang: Lang) =
    List(
      (Reason.Cheat.key, trans.cheat.txt()),
      (Reason.Comm.key, trans.insult.txt()),
      (Reason.Boost.key, trans.ratingManipulation.txt()),
      (Reason.Comm.key, trans.troll.txt()),
      (Reason.Other.key, trans.other.txt())
    )

  def translatedModeChoices(implicit lang: Lang) =
    List(
      (Mode.Casual.id.toString, trans.casual.txt(), none),
      (Mode.Rated.id.toString, trans.rated.txt(), none)
    )

  def translatedIncrementChoices(implicit lang: Lang) =
    List(
      (1, trans.yes.txt(), none),
      (0, trans.no.txt(), none)
    )

  def translatedModeChoicesTournament(implicit lang: Lang) =
    List(
      (Mode.Casual.id.toString, trans.casualTournament.txt(), none),
      (Mode.Rated.id.toString, trans.ratedTournament.txt(), none)
    )

  private val encodeId = (v: Variant) => v.id.toString
  private val encodeDisplayLibId = (lib: DisplayLib) => lib.id.toString

  private def variantTupleId = variantTuple(encodeId) _

  private def variantTuple(encode: Variant => String)(variant: Variant) =
    (encode(variant), variant.name, variant.title.some)

  //TODO: Push these lists into strategygames
  def translatedDisplayLibChoices(implicit lang: Lang): List[SelectChoice] =
    translatedDisplayLibChoices(encodeDisplayLibId)

  def translatedDisplayLibChoices(encode: DisplayLib => String)(implicit lang: Lang): List[SelectChoice] =
    List(
      (encode(DisplayLib.Chess()), DisplayLib.Chess().name, DisplayLib.Chess().name.some),
      (encode(DisplayLib.Draughts()), DisplayLib.Draughts().name, DisplayLib.Draughts().name.some),
      (encode(DisplayLib.LinesOfAction()), DisplayLib.LinesOfAction().name, DisplayLib.LinesOfAction().name.some)
    )

  def translatedChessVariantChoices(implicit lang: Lang): List[SelectChoice] =
    translatedChessVariantChoices(encodeId)

  def translatedChessVariantChoices(encode: Variant => String)(implicit lang: Lang): List[SelectChoice] =
    List(
      Variant.Chess(strategygames.chess.variant.Standard)
    ).map(variantTuple(encode))

  def translatedChessVariantChoicesWithVariants(implicit lang: Lang): List[SelectChoice] =
    translatedChessVariantChoicesWithVariants(encodeId)

  def translatedChessVariantChoicesWithVariants(
      encode: Variant => String
  )(implicit lang: Lang): List[SelectChoice] =
    translatedChessVariantChoices(encode) ::: List(
      Variant.Chess(strategygames.chess.variant.Crazyhouse),
      Variant.Chess(strategygames.chess.variant.Chess960),
      Variant.Chess(strategygames.chess.variant.KingOfTheHill),
      Variant.Chess(strategygames.chess.variant.ThreeCheck),
      Variant.Chess(strategygames.chess.variant.Antichess),
      Variant.Chess(strategygames.chess.variant.Atomic),
      Variant.Chess(strategygames.chess.variant.Horde),
      Variant.Chess(strategygames.chess.variant.RacingKings),
    ).map(variantTuple(encode))

  def translatedChessVariantChoicesWithFen(implicit lang: Lang) =
    translatedChessVariantChoices :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Chess960)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.FromPosition))

  def translatedChessAiVariantChoices(implicit lang: Lang) =
    translatedChessVariantChoices :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Crazyhouse)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Chess960)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.KingOfTheHill)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.ThreeCheck)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Antichess)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Atomic)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.Horde)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.RacingKings)) :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.FromPosition))

  def translatedChessVariantChoicesWithVariantsAndFen(implicit lang: Lang) =
    translatedChessVariantChoicesWithVariants :+
      variantTupleId(Variant.Chess(strategygames.chess.variant.FromPosition))

  def translatedDraughtsVariantChoices(implicit lang: Lang): List[SelectChoice] =
    translatedDraughtsVariantChoices(encodeId)

  def translatedDraughtsVariantChoices(encode: Variant => String)(implicit lang: Lang): List[SelectChoice] =
    List(
      Variant.Draughts(strategygames.draughts.variant.Standard)
    ).map(variantTuple(encode))

  def translatedDraughtsVariantChoicesWithVariants(implicit lang: Lang): List[SelectChoice] =
    translatedDraughtsVariantChoicesWithVariants(encodeId)

  def translatedDraughtsVariantChoicesWithVariants(
      encode: Variant => String
  )(implicit lang: Lang): List[SelectChoice] =
    translatedDraughtsVariantChoices(encode) ::: List(
      Variant.Draughts(strategygames.draughts.variant.Russian),
      Variant.Draughts(strategygames.draughts.variant.Brazilian),
      Variant.Draughts(strategygames.draughts.variant.Pool),
      Variant.Draughts(strategygames.draughts.variant.Frisian),
      Variant.Draughts(strategygames.draughts.variant.Frysk),
      Variant.Draughts(strategygames.draughts.variant.Antidraughts),
      Variant.Draughts(strategygames.draughts.variant.Breakthrough)
    ).map(variantTuple(encode))

  def translatedDraughtsVariantChoicesWithFen(implicit lang: Lang) =
    translatedDraughtsVariantChoices :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.FromPosition))

  def translatedDraughtsAiVariantChoices(implicit lang: Lang) =
    translatedDraughtsVariantChoices :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Russian)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Brazilian)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Pool)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Frisian)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Frysk)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Antidraughts)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.Breakthrough)) :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.FromPosition))

  def translatedDraughtsVariantChoicesWithVariantsAndFen(implicit lang: Lang) =
    translatedDraughtsVariantChoicesWithVariants :+
      variantTupleId(Variant.Draughts(strategygames.draughts.variant.FromPosition))

  def translatedLOAVariantChoices(implicit lang: Lang): List[SelectChoice] =
    translatedLOAVariantChoices(encodeId)

  def translatedLOAVariantChoices(encode: Variant => String)(implicit lang: Lang): List[SelectChoice] =
    List(
      Variant.Chess(strategygames.chess.variant.LinesOfAction)
    ).map(variantTuple(encode))

  def translatedLOAVariantChoicesWithVariants(implicit lang: Lang): List[SelectChoice] =
    translatedLOAVariantChoicesWithVariants(encodeId)

  def translatedLOAVariantChoicesWithVariants(
      encode: Variant => String
  )(implicit lang: Lang): List[SelectChoice] =
    translatedLOAVariantChoices(encode)

  def translatedLOAVariantChoicesWithFen(implicit lang: Lang) =
    translatedLOAVariantChoices

  def translatedLOAAiVariantChoices(implicit lang: Lang): List[SelectChoice] = List()

  def translatedLOAVariantChoicesWithVariantsAndFen(implicit lang: Lang) =
    translatedLOAVariantChoicesWithVariants

  def translatedSpeedChoices(implicit lang: Lang) =
    Speed.limited map { s =>
      val minutes = s.range.max / 60 + 1
      (
        s.id.toString,
        s.toString + " - " + trans.lessThanNbMinutes.pluralSameTxt(minutes),
        none
      )
    }

  def translatedSideChoices(implicit lang: Lang) =
    List(
      ("black", trans.black.txt(), none),
      ("random", trans.randomColor.txt(), none),
      ("white", trans.white.txt(), none)
    )

  def translatedAnimationChoices(implicit lang: Lang) =
    List(
      (Pref.Animation.NONE, trans.none.txt()),
      (Pref.Animation.FAST, trans.fast.txt()),
      (Pref.Animation.NORMAL, trans.normal.txt()),
      (Pref.Animation.SLOW, trans.slow.txt())
    )

  def translatedBoardCoordinateChoices(implicit lang: Lang) =
    List(
      (Pref.Coords.NONE, trans.no.txt()),
      (Pref.Coords.INSIDE, trans.insideTheBoard.txt()),
      (Pref.Coords.OUTSIDE, trans.outsideTheBoard.txt())
    )

  def translatedMoveListWhilePlayingChoices(implicit lang: Lang) =
    List(
      (Pref.Replay.NEVER, trans.never.txt()),
      (Pref.Replay.SLOW, trans.onSlowGames.txt()),
      (Pref.Replay.ALWAYS, trans.always.txt())
    )

  def translatedPieceNotationChoices(implicit lang: Lang) =
    List(
      (Pref.PieceNotation.SYMBOL, trans.preferences.chessPieceSymbol.txt()),
      (Pref.PieceNotation.LETTER, trans.preferences.pgnLetter.txt())
    )

  def translatedClockTenthsChoices(implicit lang: Lang) =
    List(
      (Pref.ClockTenths.NEVER, trans.never.txt()),
      (Pref.ClockTenths.LOWTIME, trans.preferences.whenTimeRemainingLessThanTenSeconds.txt()),
      (Pref.ClockTenths.ALWAYS, trans.always.txt())
    )

  def translatedMoveEventChoices(implicit lang: Lang) =
    List(
      (Pref.MoveEvent.CLICK, trans.preferences.clickTwoSquares.txt()),
      (Pref.MoveEvent.DRAG, trans.preferences.dragPiece.txt()),
      (Pref.MoveEvent.BOTH, trans.preferences.bothClicksAndDrag.txt())
    )

  def translatedTakebackChoices(implicit lang: Lang) =
    List(
      (Pref.Takeback.NEVER, trans.never.txt()),
      (Pref.Takeback.ALWAYS, trans.always.txt()),
      (Pref.Takeback.CASUAL, trans.preferences.inCasualGamesOnly.txt())
    )

  def translatedMoretimeChoices(implicit lang: Lang) =
    List(
      (Pref.Moretime.NEVER, trans.never.txt()),
      (Pref.Moretime.ALWAYS, trans.always.txt()),
      (Pref.Moretime.CASUAL, trans.preferences.inCasualGamesOnly.txt())
    )

  def translatedAutoQueenChoices(implicit lang: Lang) =
    List(
      (Pref.AutoQueen.NEVER, trans.never.txt()),
      (Pref.AutoQueen.PREMOVE, trans.preferences.whenPremoving.txt()),
      (Pref.AutoQueen.ALWAYS, trans.always.txt())
    )

  def translatedAutoThreefoldChoices(implicit lang: Lang) =
    List(
      (Pref.AutoThreefold.NEVER, trans.never.txt()),
      (Pref.AutoThreefold.ALWAYS, trans.always.txt()),
      (Pref.AutoThreefold.TIME, trans.preferences.whenTimeRemainingLessThanThirtySeconds.txt())
    )

  def submitMoveChoices(implicit lang: Lang) =
    List(
      (Pref.SubmitMove.NEVER, trans.never.txt()),
      (Pref.SubmitMove.CORRESPONDENCE_ONLY, trans.preferences.inCorrespondenceGames.txt()),
      (Pref.SubmitMove.CORRESPONDENCE_UNLIMITED, trans.preferences.correspondenceAndUnlimited.txt()),
      (Pref.SubmitMove.ALWAYS, trans.always.txt())
    )

  def confirmResignChoices(implicit lang: Lang) =
    List(
      (Pref.ConfirmResign.NO, trans.no.txt()),
      (Pref.ConfirmResign.YES, trans.yes.txt())
    )

  def translatedRookCastleChoices(implicit lang: Lang) =
    List(
      (Pref.RookCastle.NO, trans.preferences.castleByMovingTwoSquares.txt()),
      (Pref.RookCastle.YES, trans.preferences.castleByMovingOntoTheRook.txt())
    )

  def translatedChallengeChoices(implicit lang: Lang) =
    List(
      (Pref.Challenge.NEVER, trans.never.txt()),
      (
        Pref.Challenge.RATING,
        trans.ifRatingIsPlusMinusX.txt(lila.pref.Pref.Challenge.ratingThreshold)
      ),
      (Pref.Challenge.FRIEND, trans.onlyFriends.txt()),
      (Pref.Challenge.ALWAYS, trans.always.txt())
    )

  def translatedMessageChoices(implicit lang: Lang) =
    List(
      (Pref.Message.NEVER, trans.onlyExistingConversations.txt()),
      (Pref.Message.FRIEND, trans.onlyFriends.txt()),
      (Pref.Message.ALWAYS, trans.always.txt())
    )

  def translatedStudyInviteChoices(implicit lang: Lang) = privacyBaseChoices
  def translatedPalantirChoices(implicit lang: Lang)    = privacyBaseChoices
  private def privacyBaseChoices(implicit lang: Lang) =
    List(
      (Pref.StudyInvite.NEVER, trans.never.txt()),
      (Pref.StudyInvite.FRIEND, trans.onlyFriends.txt()),
      (Pref.StudyInvite.ALWAYS, trans.always.txt())
    )

  def translatedInsightShareChoices(implicit lang: Lang) =
    List(
      (Pref.InsightShare.NOBODY, trans.withNobody.txt()),
      (Pref.InsightShare.FRIENDS, trans.withFriends.txt()),
      (Pref.InsightShare.EVERYBODY, trans.withEverybody.txt())
    )

  def translatedBoardResizeHandleChoices(implicit lang: Lang) =
    List(
      (Pref.ResizeHandle.NEVER, trans.never.txt()),
      (Pref.ResizeHandle.INITIAL, trans.preferences.onlyOnInitialPosition.txt()),
      (Pref.ResizeHandle.ALWAYS, trans.always.txt())
    )

  def translatedBlindfoldChoices(implicit lang: Lang) =
    List(
      Pref.Blindfold.NO  -> trans.no.txt(),
      Pref.Blindfold.YES -> trans.yes.txt()
    )
}
