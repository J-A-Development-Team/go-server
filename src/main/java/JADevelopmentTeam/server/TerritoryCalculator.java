package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;

import java.util.ArrayList;

public abstract class TerritoryCalculator {
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
                    TerritoryStates resultTerritory = calculate(territories, j, i, size, 0);
                    for (int k = 0; k < size; k++) {
                        for (int m = 0; m < size; m++) {
                            if (resultTerritory == TerritoryStates.None) {
                                if (territories[m][k] == TerritoryStates.ProbablyBlack || territories[m][k] == TerritoryStates.ProbablyWhite || territories[m][k] == TerritoryStates.Verified) {
                                    territories[m][k] = TerritoryStates.None;
                                }
                            } else if (resultTerritory == TerritoryStates.ProbablyBlack) {
                                if (territories[m][k] == TerritoryStates.ProbablyBlack || territories[m][k] == TerritoryStates.Verified) {
                                    territories[m][k] = TerritoryStates.BlackTerritory;
                                }
                            } else if (resultTerritory == TerritoryStates.ProbablyWhite){
                                if (territories[m][k] == TerritoryStates.ProbablyWhite || territories[m][k] == TerritoryStates.Verified) {
                                    territories[m][k] = TerritoryStates.WhiteTerritory;
                                }
                            }
                        }

                    }
                }
            }
        }
        return territories;
    }

    private static int changePossible(int x, int y, int size, Integer possible, TerritoryStates[][] territories) {
        ArrayList<TerritoryStates> nearbyTerritory = new ArrayList<>();
        if (x < size - 1) {
            nearbyTerritory.add(territories[x + 1][y]);
        }
        if (y < size - 1) {
            nearbyTerritory.add(territories[x][y + 1]);
        }
        if (y > 0) {
            nearbyTerritory.add(territories[x][y - 1]);
        }
        if (x > 0) {
            nearbyTerritory.add(territories[x - 1][y]);
        }
        switch (possible) {
            case -1:
                if (nearbyTerritory.contains(TerritoryStates.Black) || nearbyTerritory.contains(TerritoryStates.ProbablyBlack))
                    possible = -2;
                break;
            case 0:
                if ((nearbyTerritory.contains(TerritoryStates.Black) || nearbyTerritory.contains(TerritoryStates.ProbablyBlack)) && !(nearbyTerritory.contains(TerritoryStates.White) || nearbyTerritory.contains(TerritoryStates.ProbablyWhite))) {
                    possible = 1;
                } else if ((nearbyTerritory.contains(TerritoryStates.White) || nearbyTerritory.contains(TerritoryStates.ProbablyWhite)) && !(nearbyTerritory.contains(TerritoryStates.Black) || nearbyTerritory.contains(TerritoryStates.ProbablyBlack))) {
                    possible = -1;
                    break;
                }
            case 1:
                if (nearbyTerritory.contains(TerritoryStates.White) || nearbyTerritory.contains(TerritoryStates.ProbablyWhite))
                    possible = -2;
        }
        return possible;
    }

    //    possible:
//    1 black
//    0 unknown
//    -1 white
    // -2 none for 100%
    private static TerritoryStates calculate(TerritoryStates[][] territories, int x, int y, int size, Integer possible) {
        possible = changePossible(x,y,size,possible,territories);
        if (territories[x][y] != TerritoryStates.Unknown || territories[x][y] == TerritoryStates.Verified)
            return territories[x][y];

        territories[x][y] = TerritoryStates.Verified;

        ArrayList<TerritoryStates> nearbyTerritory = new ArrayList<>();


        if (x < size - 1) {
            TerritoryStates tempTerritory = calculate(territories, x + 1, y, size, possible);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (y < size - 1) {
            TerritoryStates tempTerritory = calculate(territories, x, y + 1, size, possible);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (y > 0) {
            TerritoryStates tempTerritory = calculate(territories, x, y - 1, size, possible);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (x > 0) {
            TerritoryStates tempTerritory = calculate(territories, x - 1, y, size, possible);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (nearbyTerritory.size() == 0) {
            switch (possible) {
                case -1:
                    territories[x][y] = TerritoryStates.ProbablyWhite;
                    break;
                case -2:
                case 0:
                    territories[x][y] = TerritoryStates.None;
                    break;
                case 1:
                    territories[x][y] = TerritoryStates.ProbablyBlack;

            }
            return territories[x][y];
        }
        boolean blackExpected;
        blackExpected = (nearbyTerritory.get(0) == TerritoryStates.Black || nearbyTerritory.get(0) == TerritoryStates.ProbablyBlack);

        for (TerritoryStates territoryStates : nearbyTerritory) {
            if (blackExpected) {
                if (!(territoryStates == TerritoryStates.ProbablyBlack || territoryStates == TerritoryStates.Black)) {
                    territories[x][y] = TerritoryStates.None;
                    return territories[x][y];
                }
            } else {
                if (!(territoryStates == TerritoryStates.ProbablyWhite || territoryStates == TerritoryStates.White)) {
                    territories[x][y] = TerritoryStates.None;
                    return territories[x][y];
                }
            }
        }

        if (blackExpected) {
            territories[x][y] = TerritoryStates.ProbablyBlack;
        } else {
            territories[x][y] = TerritoryStates.ProbablyWhite;
        }
        return territories[x][y];
    }


}
