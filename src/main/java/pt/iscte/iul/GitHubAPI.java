package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

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

        /**
         * @return A YYYY-MM-DD formatted string.
         */
        @Override
        public String toString() {
            return formatted;
        }
    }

    private static class Repo {
        private String created_at;

        public String getCreatedAt() {
            return this.created_at;
        }
    }

    /**
     * @return A {@link Date} object with the repository creation date.
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
     * Stores relevant information about a collaborator.
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

        /**
         * @return The collaborator's name.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * @return An array of {@link Collaborators}.
     * @throws IOException If the request fails.
     */
    public Collaborators[] getCollaborators() throws IOException {
        var request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl + "/collaborators").build();

        var resp = this.httpClient.newCall(request).execute();

        var ret = this.mapper.readValue(Objects.requireNonNull(resp.body()).string(), Collaborators[].class);
        for (int i = 0; i < ret.length; i++) {
            resp = this.httpClient.newCall(
                    new Request.Builder().url("https://api.github.com/users/" + ret[i].login).build()
            ).execute();

            var mapper2 = this.mapper.readValue(Objects.requireNonNull(resp.body()).string(), User.class);
            ret[i].name = mapper2.getName();
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

        return Objects.requireNonNull(resp.body()).string();
    }

    /**
     * Stores the branch name.
     * The JSON response is unloaded directly into this object.
     */
    public static class Branch {
        private String name;

        /**
         * @return The name of the branch.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Function that retrieves all the repository branches.
     * @return An array of {@link Branch} objects.
     * @throws IOException
     */
    public Branch[] getBranches() throws IOException {
        var request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .url(this.baseAPIUrl + "/branches").build();

        Response resp = this.httpClient.newCall(request).execute();
        return this.mapper.readValue(resp.body().string(), Branch[].class);
    }

    /**
     * Stores imporant data about a commit.
     */
    public static class CommitData {
        private Date date;
        private String description;

        public CommitData(Date date, String description) {
            this.date = date;
            this.description = description;
        }

        /**
         * @return Commit date in a {@link Date} object.
         */
        public Date getDate() {
            return date;
        }

        /**
         * @return Commit message.
         */
        public String getMessage() {
            return description;
        }
    }

    /**
     * Stores a user and their commits ordered from oldest to newest.
     */
    public static class Commits {
        private final String user;
        private final List<CommitData> commitData = new ArrayList<>();

        public Commits(String user, List<Commit> commits) {
            this.user = user;

            for (var commit : commits) {
                this.commitData.add(
                        new CommitData(
                                commit.getCommitDate(),
                                commit.getCommitMessage()
                        )
                );
            }

            Collections.reverse(this.commitData);
        }

        /**
         * Can be empty, see "user" in {@link GitHubAPI#getCommits(String, String)}
         * @return The committer's name.
         */
        public String getCommitter() {
            return this.user;
        }

        public List<CommitData> getCommitData() {
            return commitData;
        }
    }

    private static class Commit {
        private Date commitDate;
        private String commitMessage;

        @SuppressWarnings("unchecked")
        @JsonProperty("commit")
        private void unpackNested(Map<String,Object> commit) {
            this.commitMessage = (String)commit.get("message");
            Map<String,String> committer = (Map<String,String>)commit.get("committer");
            this.commitDate = new Date(committer.get("date"));
        }

        public Date getCommitDate() {
            return commitDate;
        }

        public String getCommitMessage() {
            return commitMessage;
        }
    }

    /**
     * Retrieves commits per branch per user, if "user" is empty retrieves all the commits in the branch.
     * @param branch Branch name.
     * @param user The username of the user in question, can be empty.
     * @return A {@link Commits} object.
     * @throws IOException
     */
    public Commits getCommits(String branch, String user) throws IOException {
        var currentPage = 1;
        List<Commit> commitBuffer = new ArrayList<>();
        // this way we can easily set the page number
        var formattableUrl = this.baseAPIUrl + "/commits?" + (Objects.equals(user, "") ? "" : "&author=" + user) + "&sha=" + branch + "&page=%d";

        while (true) {
            var request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .url(String.format(formattableUrl, currentPage++)).build();

            Response resp = this.httpClient.newCall(request).execute();

            var ret = Arrays.asList(
                    this.mapper.readValue(Objects.requireNonNull(resp.body()).string(), Commit[].class)
            );
            if (ret.isEmpty()) {
                break;
            }

            commitBuffer.addAll(ret);
        }

        return new Commits(user, commitBuffer);
    }
}
