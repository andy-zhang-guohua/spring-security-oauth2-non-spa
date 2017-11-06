package andy.barkyard.oauth2.live;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class AuthLiveTest {

    private String refreshToken;
    private final String oauth2_server_url="http://localhost:9199";

    private String obtainAccessToken(String clientId, String client_secret, String username, String password) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("username", username);
        params.put("password", password);
        final Response response = RestAssured.given().auth().preemptive().basic(clientId, client_secret).and().with().params(params).when().post(oauth2_server_url+"/oauth/token");
        refreshToken = response.jsonPath().getString("refresh_token");
        return response.jsonPath().getString("access_token");
    }

    private String obtainRefreshToken(String clientId, String client_secret) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", clientId);
        params.put("refresh_token", refreshToken);
        final Response response = RestAssured.given().auth().preemptive().basic(clientId, client_secret).and().with().params(params).when().post(oauth2_server_url+"/oauth/token");
        return response.jsonPath().getString("access_token");
    }

    private void authorizeClient(String clientId, String client_secret) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("scope", "read,write");
        final Response response = RestAssured.given().auth().preemptive().basic(clientId, client_secret).and().with().params(params).when().post(oauth2_server_url+"/oauth/authorize");
    }

    @Test
    public void testObtainToken() {
        // obtain token by client id, client secret, user name, password
        final String accessToken = obtainAccessToken("biz", "secret", "john", "123");
        assertNotNull(accessToken);


    }

}