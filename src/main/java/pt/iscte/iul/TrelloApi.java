package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrelloApi {
    private final String apiKey;
    private final String apiToken;
    private final String boarName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String boardId = "614df1d076293f6b763c1c9c";

    public TrelloApi(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boarName = boardName;

        //TODO: Function to get the id of the board giving the name of the board
        this.baseAPIUrl = "https://api.trello.com/1/members/me/boards?key=" + apiKey + "&token=" + apiToken;

        this.httpClient = new OkHttpClient();
    }

    public static class Board {
        private String name;
        private String id;
        private String url;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getUrl() {
            return this.url;
        }
    }

    public static class List {
        private String name;
        private String id;
        private String url;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getUrl() {
            return this.url;
        }
    }

    // TODO: The class Card need more attributes for the rest of the project
    public static class Card {
        private String name;
        private String id;
        private String url;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getUrl() {
            return this.url;
        }
    }

    // Function to access every user's boards
    public Board[] getBoards() throws IOException {
        //HTTP request to acess every user's boards
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.baseAPIUrl).build();

        Response response = this.httpClient.newCall(request).execute();
        // Print response url
        System.out.println(response);

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Board[].class);
    }

    // Function for HTTP request for components
    private Response HTTPRequest(String component, String componentId) throws IOException {
        //HTTP request to access the board
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.boardURL + componentId + "/" + component + "?key=" + apiKey + "&token=" + apiToken).build();

        Response response = this.httpClient.newCall(request).execute();
        System.out.println(response);
        return response;
    }

    // Function to return the Board that the user specified at login
    public Board getBoard(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(response.body().string(), Board.class);
    }

    // Function to return all the lists in the board
    public List[] getBoardLists(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("lists", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), List[].class);
    }

    // Function to return all the lists in the board
    public Card[] getBoardCards(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }


    public static void get_info(String[] user_git_info, String[] user_trello_info) throws IOException {
        TrelloApi trello = new TrelloApi(user_trello_info[0], user_trello_info[1], user_trello_info[2]);
        //get all boards from the user
        var boards = trello.getBoards();
        String boardId = null;
        // Iterate over all boards to find the one with the same name as prompted at login
        for (Board info : boards) {
            if (info.name.equals(user_trello_info[0])) {
                // If the key name is the same, get the id of the board
                boardId = info.id;
            }
        }
        // Board Specified by user at Login
        Board board = trello.getBoard(boardId);
        System.out.println(board.getName());
        //iterate over all lists and print everyone to check if it's correct
        var lists = trello.getBoardLists(boardId);
        for (List info: lists){
            System.out.println(info.getName());
        }
        //iterate over all cards on the board
        var cards = trello.getBoardCards(boardId);
        for (Card info: cards){
            System.out.println(info.getName());
        }
    }
}