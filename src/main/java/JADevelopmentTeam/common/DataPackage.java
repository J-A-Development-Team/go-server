package JADevelopmentTeam.common;

import java.io.Serializable;

public class DataPackage implements Serializable {
    private Object data;
    private Info info;
    public enum Info {Stone,StoneTable,PlayerColor,Info
    }

    public DataPackage(Object data, Info info) {
        this.data = data;
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public Info getInfo() {
        return info;
    }
}
