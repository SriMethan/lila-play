type Board = { pieces: { [key: number]: string }; turn: boolean };
export type SanToUci = { [key: string]: Uci };

function decomposeUci(uci: string): Array<Uci> {
  return [uci.slice(0, 2), uci.slice(2, 4)];
}

function readFen(fen: string) {
  var fenParts = fen.split(':');
  var board: Board = {
    pieces: {},
    turn: fenParts[0] === 'W',
  };

  for (var i = 0; i < fenParts.length; i++) {
    var clr = fenParts[i].slice(0, 1);
    if ((clr === 'W' || clr === 'B') && fenParts[i].length > 1) {
      var fenPieces = fenParts[i].slice(1).split(',');
      for (var k = 0; k < fenPieces.length; k++) {
        var fieldNumber = fenPieces[k].slice(1),
          role = fenPieces[k].slice(0, 1);
        if (fieldNumber.length !== 0 && role.length !== 0) {
          if (fieldNumber.length == 1) fieldNumber = '0' + fieldNumber;
          let fieldNumberNumber = parseInt(fieldNumber);
          board.pieces[fieldNumberNumber] = role;
        }
      }
    }
  }

  return board;
}

function shorten(uci: string) {
  return uci && uci.startsWith('0') ? uci.slice(1) : uci;
}

function sanOf(_board: Board, uci: string, capture: boolean) {
  var move = decomposeUci(uci);
  if (capture) return shorten(move[0]) + 'x' + shorten(move[1]);
  else return shorten(move[0]) + '-' + shorten(move[1]);
}

export default function sanWriter(fen: string, ucis: string[], captLen?: number): SanToUci {
  const board = readFen(fen);
  var capture = !!captLen && captLen > 0;
  const sans: SanToUci = {};
  ucis.forEach(function (uci) {
    const san = sanOf(board, uci, capture);
    sans[san] = uci;
  });
  return sans;
}