package Server.Comparators;

import Server.Heartbeat;

import java.util.Comparator;

/**
 * Compara dois Heartbeats pela versão da base de dados
 */
public class HeartbeatComparatorDBVersion implements Comparator<Heartbeat> {

    /**
     * Compara dois Heartbeats pela versão da base de dados
     * @param o1 Primeiro Heartbeat
     * @param o2 Segundo Heartbeat
     * @return 0 se tiverem a mesma versão, 1 se a versão de o1 for mais recente do que a de o2, -1 se a versão de o2 for mais recente do que a de o1
     */
    @Override
    public int compare(Heartbeat o1, Heartbeat o2) {
        return Integer.compare(o1.getDbVersion(), o2.getDbVersion());
    }
}
