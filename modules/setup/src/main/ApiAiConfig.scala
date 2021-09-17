package lila.setup

import strategygames.Clock
import strategygames.Color.{ Black, White }
import strategygames.{ Game => StratGame, DisplayLib }
import strategygames.variant.Variant
import strategygames.format.FEN
import strategygames.chess.variant.{ FromPosition }

import lila.game.{ Game, Player, Pov, Source }
import lila.lobby.Color
import lila.user.User

final case class ApiAiConfig(
    variant: Variant,
    fenVariant: Option[Variant],
    clock: Option[Clock.Config],
    daysO: Option[Int],
    color: Color,
    level: Int,
    fen: Option[FEN] = None
) extends Config
    with Positional {

  val strictFen = false

  val days      = ~daysO
  val increment = clock.??(_.increment.roundSeconds)
  val time      = clock.??(_.limit.roundSeconds / 60)
  val timeMode =
    if (clock.isDefined) TimeMode.RealTime
    else if (daysO.isDefined) TimeMode.Correspondence
    else TimeMode.Unlimited

  def game(user: Option[User]) =
    fenGame { chessGame =>
      val perfPicker = lila.game.PerfPicker.mainOrDefault(
        strategygames.Speed(chessGame.clock.map(_.config)),
        chessGame.situation.board.variant,
        makeDaysPerTurn
      )
      Game
        .make(
          chess = chessGame,
          whitePlayer = creatorColor.fold(
            Player.make(White, user, perfPicker),
            Player.make(White, level.some)
          ),
          blackPlayer = creatorColor.fold(
            Player.make(Black, level.some),
            Player.make(Black, user, perfPicker)
          ),
          mode = strategygames.Mode.Casual,
          source = if (chessGame.board.variant.fromPosition) Source.Position else Source.Ai,
          daysPerTurn = makeDaysPerTurn,
          pgnImport = None
        )
        .sloppy
    } start

  def pov(user: Option[User]) = Pov(game(user), creatorColor)

  def autoVariant =
    if (variant.standard && fen.exists(!_.initial)) copy(variant = Variant.wrap(FromPosition))
    else this
}

object ApiAiConfig extends BaseConfig {

  // lazy val clockLimitSeconds: Set[Int] = Set(0, 15, 30, 45, 60, 90) ++ (2 to 180).view.map(60 *).toSet

  def from(
      l: Int,
      lib: Int,
      cv: Option[String],
      dv: Option[String],
      lv: Option[String],
      cl: Option[Clock.Config],
      d: Option[Int],
      c: Option[String],
      pos: Option[String]
  ) =
    new ApiAiConfig(
      variant = lib match {
        case 0 => Variant.Chess(strategygames.chess.variant.Variant.orDefault(~cv))
        case 1 => Variant.Draughts(strategygames.draughts.variant.Variant.orDefault(~dv))
        case 2 => Variant.Chess(strategygames.chess.variant.Variant.orDefault(~lv))
      },
      fenVariant = none,
      clock = cl,
      daysO = d,
      color = Color.orDefault(~c),
      level = l,
      fen = pos.map(f => FEN.apply(DisplayLib(lib).codeLib, f))
    ).autoVariant
}
