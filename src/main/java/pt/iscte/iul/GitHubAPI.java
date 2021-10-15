package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.IOException;

/**
 * @author Oleksandr Kobelyuk.
 */
public class GitHubAPI {
    private final String apiKey;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;

    /**
     * Base class for requesting information from the GitHub API.
     * @param repoOwner Owner of the repository.
     * @param projectName Name of the project.
     * @param apiKey GitHub API access key.
     */
    public GitHubAPI(String repoOwner, String projectName, String apiKey) {
        this.apiKey = apiKey;

        this.baseAPIUrl = "https://api.github.com/repos/" + repoOwner + "/" + projectName;

        this.httpClient = new OkHttpClient();
    }

    public static class Collaborators {
        private String login;
        private String avatar_url;
        private String html_url;

        public String getName() {
            return this.login;
        }

        public String getAvatar() {
            return this.avatar_url;
        }

        public String getProfile() {
            return this.html_url;
        }
    }

    /**
     * @return A list of collaborators.
     * @throws IOException
     */
    public Collaborators[] getCollaborators() throws IOException {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl + "/collaborators").build();

        Response resp = this.httpClient.newCall(request).execute();

        ObjectMapper mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(resp.body().string(), Collaborators[].class);
    }
}
