package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import javax.swing.text.TabExpander;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Duarte Casaleiro.
 */
public class TrelloAPI {
    private final String apiKey;
    private final String apiToken;
    private final String boardName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String listURL = "https://api.trello.com/1/lists/";
    //private final String boardId = "614df1d076293f6b763c1c9c";

    /**
     * Base class for requesting information from the Trello API.
     * @param boardName Name of the board.
     * @param apiKey Trello API access key.
     * @param apiToken Trello API access token.
     */
    public TrelloAPI(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boardName = boardName;

        //TODO: Function to get the id of the board giving the name of the board
        this.baseAPIUrl = "https://api.trello.com/1/members/me/boards?key=" + apiKey + "&token=" + apiToken;

        this.httpClient = new OkHttpClient();
    }

    /**
     * Board object.
     */
    public static class Board {
        private String name;
        private String id;
        private String url;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The url.
         */
        public String getUrl() {
            return this.url;
        }
    }

    /**
     * Board object.
     */
    public static class List {
        private String name;
        private String id;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }
    }

    // TODO: The class Card needs more attributes for the rest of the project
    /**
     * Card object.
     */
    public static class Card {
        private String name;
        private String id;
        private String due;
        private String desc;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The due date.
         */
        public String getDueDate() {
            return this.due.split("T")[0]; // split by delimiter T
        }

        /**
         * @return The description.
         */
        public String getDesc() {
            return this.desc;
        }
    }

    /**
     * @return a list of all boards owned by the user.
     * @throws IOException If the request fails.
     */
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

    /**
     * @return the http response.
     * @throws IOException If the request fails.
     */
    // Function for HTTP request for components
    private Response HTTPRequest(String component, String componentId, String url) throws IOException {
        //HTTP request to access the board
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(url + componentId + "/" + component + "?key=" + apiKey + "&token=" + apiToken).build();

        Response response = this.httpClient.newCall(request).execute();
        return response;
    }

    /**
     * @param boardId board id.
     * @return the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return the Board that the user specified at login
    public Board getBoard(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(response.body().string(), Board.class);
    }

    /**
     * @param boardId board id.
     * @return all lists in the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return all the lists in the board
    public List[] getBoardLists(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("lists", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), List[].class);
    }

    /**
     * @param listName name of the list.
     * @param boardId board id.
     * @return the list in the board identified by the board id.
     */
    // Function to return a specific List in the board
    public List getList(String listName, String boardId) throws IOException {
        var lists = this.getBoardLists(boardId);
        for (List list: lists){
            if (list.getName().equals(listName)){
                return list;
            }
        }
        return null;
    }

    /**
     * @param boardId id of the board.
     * @return all cards in the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return all the lists in the board
    public Card[] getBoardCards(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param listId id of the list.
     * @param boardId id of the board.
     * @return all cards in the list identified by the list id.
     * @throws IOException If the request fails.
     */
    // Function to return all the card in a specific list
    public Card[] getListCards(String listId, String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", listId, this.listURL);

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param sprintNumber number of the sprint.
     * @param boardId id of the board.
     * @return an array with the start date and the end date.
     * @throws IOException If the request fails.
     */
    public String[] getSprintDates(int sprintNumber, String boardId) throws IOException {
        // flag to see if we've found the start date
        boolean startDateFound = false;
        // initialize list of dates
        String[] dates = new String[2];
        String listName = "Sprint Ceremonies";

        // get the list of all ceremonies
        var list = this.getList(listName, boardId);
        var cards = this.getListCards(list.getId(), boardId);

        // Iterate over all cards
        for (Card c : cards) {
            // search for due date of Sprint Planning that is equal to Sprint start date
            if (c.name.equals("Sprint Planning - Sprint " + sprintNumber)) {
                dates[0] = c.getDueDate();
                startDateFound = true;
            }
            // search for due date of Sprint Retrospective that is equal to Sprint end date
            else if (c.name.equals("Sprint Retrospective - Sprint " + sprintNumber)) {
                dates[1] = c.getDueDate();
                if (startDateFound) break; // if start date is found, we can break the for loop
            }
        }
        // Returns dates list
        // dates[0] -> Sprint start date
        // dates[1] -> Sprint end date
        return dates;
    }

    // Function to get the descriptions of the Sprint Ceremonies of a specific sprint
    // TODO: Access specific cards based on a specific list to reduce search time
    public String getCeremoniesDescription(String boardId, String sprintType, int SprintNumber) throws IOException {
        // initialize list of descriptions
        //String[] descriptions = new String[3];

        var cards = this.getBoardCards(boardId);
        // Iterate over all cards
        for (Card c : cards) {
            // get the Sprint Planning description
            if (c.name.equals("Sprint " + sprintType + " - Sprint " + SprintNumber)){
                return c.getDesc();
            }
            /*
                // get the Sprint Review description
            else if (c.name.equals("Sprint Review - Sprint " + SprintNumber)) descriptions[1] = c.desc();
                // get the Sprint Retrospective description
            else if (c.name.equals("Sprint Retrospective - Sprint " + SprintNumber)) descriptions[2] = c.desc();
            */
        }
        //System.out.println("Description of Sprint Planning \n" + descriptions[0]);

        // Returns descriptions list
        // descriptions[0] -> Sprint Planning Description
        // descriptions[1] -> Sprint Review Description
        // descriptions[2] -> Sprint Retrospective Description
        //return descriptions;
        return "";
    }

    /**
     * @param boardId id of the board.
     * @param sprintNumber number of the sprint.
     * @return an ArrayList of all the products already done in the specific sprint.
     * @throws IOException If the request fails.
     */
    // Function to get product backlog items already done
    public ArrayList<String> getDoneProductBacklog(String boardId, int sprintNumber) throws IOException {
        var doneItems = new ArrayList<String>();

        String listName = "Done - Sprint " + sprintNumber;

        // get specific list
        var list = this.getList(listName, boardId);
        // get all cards from the list
        var cards = this.getListCards(list.getId(), boardId);
        for (Card card: cards){
            doneItems.add(card.name);
        }
        return doneItems;
    }

}