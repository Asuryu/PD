package Server;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Classe que representa um heartbeat
 */
public class Heartbeat implements Serializable, Comparable<Heartbeat> {

    @Serial
    private static final long serialVersionUID = 2L;

    private final String ip;
    private final int port;
    private final boolean available;
    private final int dbVersion;

    private final int activeConnections;
    private final Instant sentTimestamp;

    /**
     * Construtor da classe Heartbeat
     * @param ip IP do servidor
     * @param port Porta do servidor
     * @param available Disponibilidade do servidor
     * @param dbVersion Versão da base de dados
     * @param activeConnections Número de conexões ativas
     */
    public Heartbeat(String ip, int port, int dbVersion, int activeConnections, boolean available){
        this.ip = ip;
        this.port = port;
        this.available = available;
        this.dbVersion = dbVersion;
        this.activeConnections = activeConnections;
        this.sentTimestamp = Instant.now();
    }

    public String getIp() { return ip; }

    public int getPort() {
        return port;
    }

    public boolean isAvailable() { return available; }

    public int getDbVersion() {
        return dbVersion;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public Instant getSentTimestamp() {
        return sentTimestamp;
    }

    /**
     * Método que compara dois heartbeats
     * Serve para ordenar a lista de heartbeats
     * @param o Heartbeat a comparar
     * @return 0 se tiverem o mesmo número de conexões ativas, >=1 se o primeiro heartbeat tiver mais conexões, <=-1 se o primeiro heartbeat tiver menos conexões
     */
    @Override
    public int compareTo(Heartbeat o) {
        return this.activeConnections - o.activeConnections;
    }

    @Override
    public String toString() {
        return String.format("[ HEARTBEAT ]\nActive Connections: %d\nAvailable: %b\ndbVersion: %d\nIP: %s\nPort: %d\nTime: %d", this.activeConnections, this.available, this.dbVersion, this.ip, this.port, this.sentTimestamp.getEpochSecond());
    }

    /**
     * Método equals da classe Heartbeat
     * @param o Objeto a comparar
     * @return true se os dois objetos tiverem o mesmo porto, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Heartbeat heartbeat)) return false;
        return port == heartbeat.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port);
    }

}
