package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class Territory {
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
                    TerritoryStates resultTerritory = calculate(territories, j, i, size);
                    for (int k = 0; k < size; k++) {
                        for (int m = 0; m < size; m++) {
                            if(resultTerritory==TerritoryStates.None){
                                if(territories[m][k]==TerritoryStates.ProbablyBlack||territories[m][k]==TerritoryStates.ProbablyWhite||territories[m][k]==TerritoryStates.Verified){
                                    territories[m][k]=TerritoryStates.None;
                                }
                            }else if(resultTerritory==TerritoryStates.ProbablyBlack){
                                if(territories[m][k]==TerritoryStates.ProbablyBlack||territories[m][k]==TerritoryStates.Verified){
                                    territories[m][k]=TerritoryStates.BlackTerritory;
                                }
                            }else{
                                if(territories[m][k]==TerritoryStates.ProbablyWhite||territories[m][k]==TerritoryStates.Verified){
                                    territories[m][k]=TerritoryStates.WhiteTerritory;
                                }
                            }
                        }

                    }
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


        if (x < size - 1) {
            TerritoryStates tempTerritory = calculate(territories, x + 1, y, size);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (y < size - 1) {
            TerritoryStates tempTerritory = calculate(territories, x, y + 1, size);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (y > 0) {
            TerritoryStates tempTerritory = calculate(territories, x, y - 1, size);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (x > 0) {
            TerritoryStates tempTerritory = calculate(territories, x - 1, y, size);
            if (tempTerritory != TerritoryStates.Verified) {
                nearbyTerritory.add(tempTerritory);
            }
        }
        if (nearbyTerritory.size() == 0) {
            territories[x][y] = TerritoryStates.None;
            return territories[x][y];
        }
        boolean blackExpected;
        blackExpected = (nearbyTerritory.get(0) == TerritoryStates.Black || nearbyTerritory.get(0) == TerritoryStates.ProbablyBlack);

        for (int i = 1; i < nearbyTerritory.size(); i++) {
            if (blackExpected) {
                if (nearbyTerritory.get(i) != TerritoryStates.ProbablyBlack && nearbyTerritory.get(i) != TerritoryStates.Black) {
                    territories[x][y] = TerritoryStates.None;
                    return territories[x][y];
                }
            } else {
                if (nearbyTerritory.get(i) != TerritoryStates.ProbablyWhite && nearbyTerritory.get(i) != TerritoryStates.White) {
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

    public enum TerritoryStates {
        Black, White, Unknown, None, ProbablyBlack, ProbablyWhite,WhiteTerritory,BlackTerritory, Verified
    }
}
