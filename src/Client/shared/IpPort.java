package Client.shared;

import java.io.Serializable;
import java.util.Objects;

public class IpPort implements Serializable, Cloneable{
    public final String ip;
    public final int port;

    public IpPort(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpPort ipPort = (IpPort) o;
        return port == ipPort.port &&  ip.equals(ipPort.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
