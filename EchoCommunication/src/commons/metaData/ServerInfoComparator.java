package commons.metaData;

import java.util.Comparator;

public class ServerInfoComparator implements Comparator<ServerInfo> {
    @Override
    public int compare(ServerInfo o1, ServerInfo o2) {
        return o1.getHash().compareTo(o2.getHash());
    }
}
