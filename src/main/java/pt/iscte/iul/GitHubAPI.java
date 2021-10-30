package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Oleksandr Kobelyuk.
 */
public class GitHubAPI {
    private final String apiKey;
    private final String baseAPIUrl;
    private final String baseRawUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;

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

        this.mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    }

    /**
     * Simple datetime object.
     */
    public static class Date {
        private final String formatted;
        private final String year;
        private final String month;
        private final String day;

        /**
         * @param raw The raw date string YYYY-MM-DD-Thh-mm-ssZ.
         */
        public Date(String raw) {
            this.formatted = raw.split("T")[0];
            var data = this.formatted.split("-");

            this.year = data[0];
            this.month = data[1];
            this.day = data[2];
        }

        /**
         * @return A YYYY-MM-DD formatted string.
         */
        public String getFormatted() {
            return formatted;
        }

        /**
         * @return The year.
         */
        public String getYear() {
            return this.year;
        }

        /**
         * @return The month.
         */
        public String getMonth() {
            return this.month;
        }

        /**
         * @return The day.
         */
        public String getDay() {
            return this.day;
        }
    }

    private static class Repo {
        private String created_at;

        public String getCreatedAt() {
            return this.created_at;
        }
    }

    /**
     * @return A date object, more specifically the date when the repository was created.
     * @throws IOException If the request fails.
     */
    public Date getStartTime() throws IOException {
        var request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl).build();

        var resp = this.httpClient.newCall(request).execute();

        var mapped = this.mapper.readValue(resp.body().string(), Repo.class);
        return new Date(mapped.getCreatedAt());
    }

    private static class User {
        private String name;

        /**
         * @return The collaborator's name.
         */
        public String getName() {
            return this.name;
        }
    }
    /**
     * Contains relevant information about a collaborator.
     */
    public static class Collaborators {
        private String login;
        private String avatar_url;
        private String html_url;

        private String name;

        /**
         * @return The collaborator's login.
         */
        public String getLogin() {
            return this.login;
        }

        /**
         * @return The url to the collaborator's avatar.
         */
        public String getAvatar() {
            return this.avatar_url;
        }

        /**
         * @return The url to the collaborator's github page.
         */
        public String getProfile() {
            return this.html_url;
        }

        protected void setName(String name) {
            this.name = name;
        }

        /**
         * @return The collaborator's name.
         */
        public String getName() {
            return name;
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

        var ret = this.mapper.readValue(resp.body().string(), Collaborators[].class);
        for (int i = 0; i < ret.length; i++) {
            resp = this.httpClient.newCall(
                    new Request.Builder().url("https://api.github.com/users/" + ret[i].login).build()
            ).execute();

            var mapper2 = this.mapper.readValue(resp.body().string(), User.class);

            ret[i].setName(mapper2.getName());
        }

        return ret;
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
