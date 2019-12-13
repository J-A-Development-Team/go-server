package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class Territory {
    public enum TerritoryStates {
        Black, White, Unknown, None, ProbablyBlack, ProbablyWhite, Verified
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

    private static TerritoryStates calculate(TerritoryStates[][] territories, int x, int y, int size) {
        if (territories[x][y] != TerritoryStates.Unknown || territories[x][y] == TerritoryStates.Verified)
            return territories[x][y];

        territories[x][y] = TerritoryStates.Verified;

        ArrayList<TerritoryStates> nearbyTerritory = new ArrayList<>();
        if (x > 0)
            nearbyTerritory.add(calculate(territories, x - 1, y, size));
        if (x < size - 2)
            nearbyTerritory.add(calculate(territories, x + 1, y, size));
        if (y < size - 2)
            nearbyTerritory.add(calculate(territories, x, y + 1, size));
        if (y > 0)
            nearbyTerritory.add(calculate(territories, x, y - 1, size));

        for (int i = nearbyTerritory.size() - 1; i >= 0; i--) {
            if (nearbyTerritory.get(i)==TerritoryStates.ProbablyBlack){
                nearbyTerritory.set(i,TerritoryStates.Black);
            } else if (nearbyTerritory.get(i) == TerritoryStates.ProbablyWhite){
                nearbyTerritory.set(i,TerritoryStates.White);
            }
            if (nearbyTerritory.get(i)==TerritoryStates.Verified){
                nearbyTerritory.remove(nearbyTerritory.get(i));
            }
        }

        if (nearbyTerritory.size() == 0){
            territories[x][y] = TerritoryStates.None;
            return territories[x][y];
        }

        for (int i = 1; i < nearbyTerritory.size(); i++) {
            if (nearbyTerritory.get(i) != nearbyTerritory.get(0)) {

                territories[x][y] = TerritoryStates.None;
                return territories[x][y];
            }
        }

        if (nearbyTerritory.get(0)==TerritoryStates.Black){
            territories[x][y] = TerritoryStates.ProbablyBlack;
        } else if (nearbyTerritory.get(0) == TerritoryStates.White){
            territories[x][y] = TerritoryStates.ProbablyWhite;
        } else {
            territories[x][y] = nearbyTerritory.get(0);
        }
        return territories[x][y];
    }
}
