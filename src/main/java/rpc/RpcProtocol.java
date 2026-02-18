package rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * Protocolo de comunicación RPC en texto plano.
 *
 * Formato request:  id=<uuid>;method=add;params=2,3
 * Formato response: id=<uuid>;ok=true;result=5
 *                   id=<uuid>;ok=false;error=<mensaje>
 */
public final class RpcProtocol {

    private RpcProtocol() {}

    public static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        String[] parts = line.split(";");
        for (String p : parts) {
            int eq = p.indexOf('=');
            if (eq > 0) {
                String k = p.substring(0, eq).trim();
                String v = p.substring(eq + 1).trim();
                map.put(k, v);
            }
        }
        return map;
    }

    public static String buildResponse(String id, boolean ok, String result, String error) {
        if (ok) {
            return "id=" + id + ";ok=true;result=" + result;
        }
        return "id=" + id + ";ok=false;error=" + sanitize(error);
    }

    private static String sanitize(String s) {
        if (s == null) return "";
        return s.replace(";", ",");
    }
}