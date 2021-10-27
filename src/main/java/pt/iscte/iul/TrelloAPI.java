package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.IOException;

public class TrelloAPI {
    private final String apiKey;
    private final String apiToken;
    private final String boardName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String boardId = "614df1d076293f6b763c1c9c";

    public TrelloAPI(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boardName = boardName;

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

    // TODO: The class Card needs more attributes for the rest of the project
    public static class Card {
        private String name;
        private String id;
        private String due;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getDueDate() {
            return this.due;
        }
    }

    // Function to access every user's boards
    public Board[] getBoards() throws IOException {
        //HTTP request to access every user's boards
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.baseAPIUrl).build();

        Response response = this.httpClient.newCall(request).execute();

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

    public static void main(String[] args) throws IOException {
        TrelloAPI trello = new TrelloAPI("ES-LETI-1Sem-2021-Grupo10",
                "71ba1d885267584d4febd7880c3074cc",
                "e2c0eeb43d666b0a273e87946fa5b7d825b659861be9a2e500a908d839a7fd0e");

        String projectId = null;
        var boards = trello.getBoards(); //get all boards

        // search for the project board
        for (Board b: boards){
            if (b.name.equals(trello.boardName)){
                projectId = b.id; // board ID
            }
        }

        // TODO: Replace "sprintNum" with an iterator to apply to all sprints
        int sprintNum = 1;
        String sprintStartDate, sprintEndDate;
        // get all board cards
        // TODO: Access specific cards based on a specific list to reduce search time
        var cards = trello.getBoardCards(projectId);
        for (Card c: cards){
            if (c.name.equals("Sprint Planning - Sprint " + sprintNum)) { // search for sprint 1
                sprintStartDate = c.due.split("T")[0]; // split by delimiter T
                System.out.println("Sprint " + sprintNum + " (Start Date) - " + sprintStartDate); // print sprint 1 start date
            }
            if (c.name.equals("Sprint Review - Sprint " + sprintNum)) { // search for sprint 1
                sprintEndDate = c.due.split("T")[0]; // split by delimiter T
                System.out.println("Sprint " + sprintNum + " (End Date) - " + sprintEndDate); // print sprint 1 end date
                break; // Stop searching after all dates are presented
            }
        }

        /*
        // get all board lists
        // TODO: Access specific cards based on a specific list to reduce search time
        var lists = trello.getBoardLists(projectId);
        for (List l: lists) {
            if (l.name.equals("Sprint Ceremonies")) // search for sprint ceremonies
                // TODO: Implement a function that returns the cards on a list
                for (Card c: l.getCards()) {
                    if (c.name.equals("Sprint Planning - Sprint " + sprintNum)) { // search for sprint 1
                        sprintStartDate = c.due.split("T")[0]; // split by delimiter T
                        System.out.println("Sprint " + sprintNum + " (Start Date) - " + sprintStartDate); // print sprint 1 start date
                    }
                    if (c.name.equals("Sprint Review - Sprint " + sprintNum)) { // search for sprint 1
                        sprintEndDate = c.due.split("T")][0]; // split by delimiter T
                        System.out.println("Sprint " + sprintNum + " (End Date) - " + sprintEndDate); // print sprint 1 end date
                    }
                }
        }
        */

    }
}