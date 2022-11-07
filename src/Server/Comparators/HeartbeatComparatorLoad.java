package Server.Comparators;

import Server.Heartbeat;

import java.util.Comparator;

/**
 * Compara dois Heartbeats pelo número de conexões ativas (carga)
 */
public class HeartbeatComparatorLoad implements Comparator<Heartbeat> {

    /**
     * Compara dois Heartbeats pelo número de conexões ativas (carga)
     * @param o1 Primeiro Heartbeat
     * @param o2 Segundo Heartbeat
     * @return 0 se tiverem o mesmo número de conexões ativas, 1 se o1 tiver mais conexões ativas que o2, -1 se o1 tiver menos conexões ativas que o2
     */
    @Override
    public int compare(Heartbeat o1, Heartbeat o2) {
        return Integer.compare(o1.getActiveConnections(), o2.getActiveConnections());
    }
}
