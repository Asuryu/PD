package Server;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Heartbeat implements Serializable, Comparable<Heartbeat> {

    @Serial
    private static final long serialVersionUID = 2L;

    private final int port;
    private final boolean available;
    private final float dbVersion;
    private final int activeConnections;

    public Heartbeat(int port, float dbVersion, int activeConnections, boolean available){
        this.port = port;
        this.available = available;
        this.dbVersion = dbVersion;
        this.activeConnections = activeConnections;
    }

    public int getPort() {
        return port;
    }

    public boolean isAvailable() {
        return available;
    }

    public float getDbVersion() {
        return dbVersion;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    @Override
    public int compareTo(Heartbeat o) {
        return this.activeConnections - o.activeConnections;
    }

    @Override
    public String toString() {
        return String.format("[ HEARTBEAT ] Active Connections: %d, Available: %b, dbVersion: %f, Port: %d", this.activeConnections, this.available, this.dbVersion, this.port);
    }

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