package sockets;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String path;
    private String queryString;
    private Map<String, String> queryParams;

    public Request(String path, String queryString) {
        this.path = path;
        this.queryString = queryString;
        this.queryParams = new HashMap<>();
        parseQueryParams();
    }

    private void parseQueryParams() {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";
            queryParams.put(key, value);
        }
    }

    public String getValues(String name) {
        return queryParams.getOrDefault(name, "");
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
