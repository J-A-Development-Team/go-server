package JADevelopmentTeam.server;

import JADevelopmentTeam.common.GameConfig;
import JADevelopmentTeam.server.Bot.Bot;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LobbyTest {

    @Test
    public void isSingleton() {
        Assert.assertEquals(Lobby.getInstance(new Object()),Lobby.getInstance(new Object()));
    }

    @Test
    public void addPlayerToLobby() {
        Lobby lobby = Lobby.getInstance(new Object());
        Player player = new Bot();
        lobby.addPlayerToLobby(player);
        Assert.assertEquals(1,lobby.playersChilling.size());
    }
    @Test
    public void groupPlayersIntoGames(){
        Lobby lobby = Lobby.getInstance(new Object());
        Player player = mock(Human.class);
        Player player1 = mock(Human.class);
        lobby.addPlayerToLobby(player);
        lobby.addPlayerToLobby(player1);
        GameConfig gameConfig = new GameConfig(false,9,false);
        when(player.getGameConfig()).thenReturn(gameConfig);
        when(player.getPlayerState()).thenReturn(Player.PlayerState.WaitForStart);
        when(player1.getGameConfig()).thenReturn(gameConfig);
        when(player1.getPlayerState()).thenReturn(Player.PlayerState.WaitForStart);
        lobby.processChillingPlayers();
        lobby.groupPlayersIntoGames();
        Assert.assertEquals(1,lobby.gamesLobby.size());
    }
}