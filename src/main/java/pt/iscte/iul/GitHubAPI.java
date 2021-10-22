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
    private final String baseRawUrl;
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
        this.baseRawUrl = "https://raw.githubusercontent.com/" + repoOwner + "/" + projectName;

        this.httpClient = new OkHttpClient();
    }

    public static class Date {
        private String year;
        private String month;
        private String day;

        public Date(String y, String m, String d) {
            this.year = y;
            this.month = m;
            this.day = d;
        }

        public String getYear() {
            return this.year;
        }

        public String getMonth() {
            return this.month;
        }

        public String getDay() {
            return this.day;
        }
    }
    public static class Repo {
        private String created_at;

        public String getCreatedAt() {
            return this.created_at;
        }
    }
    public Date getStartTime() throws IOException {
        var request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl).build();

        var resp = this.httpClient.newCall(request).execute();

        var mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        var mapped = mapper.readValue(resp.body().string(), Repo.class);
        var data = mapped.getCreatedAt().split("T")[0].split("-");

        return new Date(data[0], data[1], data[2]);
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
     * @throws IOException If the request fails.
     */
    public Collaborators[] getCollaborators() throws IOException {
        var request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl + "/collaborators").build();

        var resp = this.httpClient.newCall(request).execute();

        var mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(resp.body().string(), Collaborators[].class);
    }

    /**
     * @param branch Branch name.
     * @param path Path of file(from root) in the branch.
     * @return File contents if it exists, otherwise '404: Not Found'.
     * @throws IOException If the request fails.
     */
    public String getFile(String branch, String path) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseRawUrl + "/" + branch + path).build();

        Response resp = this.httpClient.newCall(request).execute();

        return resp.body().string();
    }
}
