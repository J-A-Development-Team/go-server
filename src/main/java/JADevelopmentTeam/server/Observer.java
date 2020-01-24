package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;

public interface Observer {
    public void update(DataPackage dataPackage);
    public void delete();
}
