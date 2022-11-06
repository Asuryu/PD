package Server.Comparators;

import Server.Heartbeat;

import java.util.Comparator;

public class HeartbeatComparatorLoad implements Comparator<Heartbeat> {
    @Override
    public int compare(Heartbeat o1, Heartbeat o2) {
        return Integer.compare(o1.getActiveConnections(), o2.getActiveConnections());
    }
}
