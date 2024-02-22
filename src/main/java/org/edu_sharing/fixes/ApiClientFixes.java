package org.edu_sharing.fixes;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.edu_sharing.generated.repository.backend.services.rest.client.ApiCallback;
import org.edu_sharing.generated.repository.backend.services.rest.client.ApiClient;
import org.edu_sharing.generated.repository.backend.services.rest.client.ApiException;
import org.edu_sharing.generated.repository.backend.services.rest.client.Pair;
import org.edu_sharing.generated.repository.backend.services.rest.client.auth.ApiKeyAuth;
import org.edu_sharing.generated.repository.backend.services.rest.client.auth.Authentication;
import org.edu_sharing.generated.repository.backend.services.rest.client.auth.HttpBasicAuth;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiClientFixes extends ApiClient {

    private final Map<String, Authentication> authentications = new HashMap<>();

    public ApiClientFixes() {
        super();
        init();
    }

    public ApiClientFixes(OkHttpClient client) {
        super(client);
        init();
    }


    private void init() {
        authentications.putAll(super.getAuthentications());
        authentications.putIfAbsent("basicAuth", new HttpBasicAuth());
    }

    @Override
    public Map<String, Authentication> getAuthentications() {
        return authentications;
    }

    @Override
    public Authentication getAuthentication(String authName) {
        return authentications.get(authName);
    }

    @Override
    public void setUsername(String username) {
        Iterator<Authentication> iterator = authentications.values().iterator();

        Authentication auth;
        do {
            if (!iterator.hasNext()) {
                throw new RuntimeException("No HTTP basic authentication configured!");
            }

            auth = iterator.next();
        } while (!(auth instanceof HttpBasicAuth));

        ((HttpBasicAuth) auth).setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        Iterator<Authentication> iterator = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!iterator.hasNext()) {
                throw new RuntimeException("No HTTP basic authentication configured!");
            }
            auth = iterator.next();
        } while (!(auth instanceof HttpBasicAuth));

        ((HttpBasicAuth) auth).setPassword(password);
    }

    @Override
    public void setApiKey(String apiKey) {
        Iterator<Authentication> iterator = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!iterator.hasNext()) {
                throw new RuntimeException("No API key authentication configured!");
            }

            auth = iterator.next();
        } while (!(auth instanceof ApiKeyAuth));

        ((ApiKeyAuth) auth).setApiKey(apiKey);
    }

    @Override
    public void setApiKeyPrefix(String apiKeyPrefix) {
        Iterator<Authentication> iterator = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!iterator.hasNext()) {
                throw new RuntimeException("No API key authentication configured!");
            }

            auth = iterator.next();
        } while (!(auth instanceof ApiKeyAuth));

        ((ApiKeyAuth) auth).setApiKeyPrefix(apiKeyPrefix);
    }

    @Override
    public void updateParamsForAuth(String[] authNames, List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams, String payload, String method, URI uri) throws ApiException {
        for (String authName : authNames) {
            Authentication auth = this.authentications.get(authName);
            if (auth == null) {
                throw new RuntimeException("Authentication undefined: " + authName);
            }
            auth.applyToParams(queryParams, headerParams, cookieParams, payload, method, uri);
        }

    }

    @Override
    public Call buildCall(String baseUrl, String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String[] authNames, ApiCallback callback) throws ApiException {
        headerParams.putIfAbsent("Content-Type", "application/json");
        if(authNames == null || authNames.length == 0) {
            authNames = new String[]{"basicAuth"};
        }
        return super.buildCall(baseUrl, path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, callback);
    }
}