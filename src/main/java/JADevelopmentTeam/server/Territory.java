package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class Territory {
    public enum TerritoryStates {
        Black, White, Unknown, None, ProbablyBlack, ProbablyWhite, Border
    }

    private static TerritoryStates[][] createTerritoryBoard(Intersection[][] board, int size) {
        TerritoryStates[][] territories = new TerritoryStates[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Intersection selected = board[j][i];
                if (selected.isHasStone() && !selected.isStoneDead()) {
                    if (selected.isStoneBlack()) {
                        territories[j][i] = TerritoryStates.Black;
                    } else {
                        territories[j][i] = TerritoryStates.White;
                    }
                } else {
                    territories[j][i] = TerritoryStates.Unknown;
                }
            }
        }
        return territories;
    }


    public static TerritoryStates[][] calculateTerritory(Intersection[][] board, int size) {
        TerritoryStates[][] territories = createTerritoryBoard(board, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (territories[j][i] == TerritoryStates.Unknown) {
                    calculate(territories, j, i, size);
                }
            }
        }
        return territories;
    }

    //hypothetical:
    // -1 none
    // 0 white
    // 1 black
    private static TerritoryStates calculate(TerritoryStates[][] board, int x, int y, int size) {
        if (!(y < 0 || y == size || x < 0 || x == size)) {
            if (board[x][y] != TerritoryStates.Unknown) {
                ArrayList<TerritoryStates> territoryStatesArrayList = new ArrayList<>();
                territoryStatesArrayList.add(calculate(board, x-1, y, size));
                territoryStatesArrayList.add(calculate(board, x, y-1, size));
                territoryStatesArrayList.add(calculate(board, x+1, y, size));
                territoryStatesArrayList.add(calculate(board, x, y+1, size));
                TerritoryStates pom = null;
                for (TerritoryStates territoryStates: territoryStatesArrayList){
                    if (territoryStates!=TerritoryStates.Border){
                        pom = territoryStates;
                        break;
                    }
                }
                for (TerritoryStates territoryStates: territoryStatesArrayList){
                    if (territoryStates!=pom && territoryStates!=TerritoryStates.Border)
                        return TerritoryStates.None;
                }
                return pom;
            } else {
                return board[x][y];
            }
        } else {
            return TerritoryStates.Border;
        }
    }
}
