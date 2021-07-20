package lila.round

import strategygames.Replay
import strategygames.format.{ FEN, Forsyth }
import strategygames.variant.Variant
import play.api.libs.json._

import lila.socket.Step

object StepBuilder {

  private val logger = lila.round.logger.branch("StepBuilder")

  def apply(
      id: String,
      pgnMoves: Vector[String],
      variant: Variant,
      initialFen: FEN
  ): JsArray = {
    Replay.gameMoveWhileValid(strategygames.GameLib.Chess(), pgnMoves, initialFen, variant) match {
      case (init, games, error) =>
        error foreach logChessError(id)
        JsArray {
          val initStep = Step(
            ply = init.turns,
            move = none,
            fen = Forsyth.>>(strategygames.GameLib.Chess(), init),
            check = init.situation.check,
            dests = None,
            drops = None,
            crazyData = init.situation.board.crazyData
          )
          val moveSteps = games.map { case (g, m) =>
            Step(
              ply = g.turns,
              move = Step.Move(m.uci, m.san).some,
              fen = Forsyth.>>(strategygames.GameLib.Chess(), g),
              check = g.situation.check,
              dests = None,
              drops = None,
              crazyData = g.situation.board.crazyData
            )
          }
          (initStep :: moveSteps).map(_.toJson)
        }
    }
  }

  private val logChessError = (id: String) =>
    (err: String) => {
      val path = if (id == "synthetic") "analysis" else id
      logger.info(s"https://playstrategy.org/$path ${err.linesIterator.toList.headOption | "?"}")
    }
}
