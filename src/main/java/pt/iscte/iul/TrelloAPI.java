package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Duarte Casaleiro, Oleksandr Kobelyuk, Miguel Romana.
 */
public class TrelloAPI {
    private final String apiKey;
    private final String apiToken;
    private final String boardName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String cardURL = "https://api.trello.com/1/cards/";
    private final String listURL = "https://api.trello.com/1/lists/";

    /**
     * Base class for requesting information from the Trello API.
     *
     * @param boardName Name of the board.
     * @param apiKey    Trello API access key.
     * @param apiToken  Trello API access token.
     */
    public TrelloAPI(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boardName = boardName;

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
     * List object.
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

    /**
     * Card object.
     */
    public static class Card {
        private String name;
        private String id;
        private String due;
        private String desc;
        private Member member;

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
            return this.due == null ? "N/A" :
                    this.due.split("T")[0]; // split by delimiter T
        }

        /**
         * @return The description.
         */
        public String getDescription() {
            return this.desc;
        }
    }

    /**
     * Member object.
     */
    public static class Member {
        private String username;
        private String id;
        // DRAFT: Hours defined for testing
        private int estimatedHours;
        private int onGoingHours;
        private int concludedHours;

        /**
         * @return The name.
         */
        public String getName() {
            return this.username;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
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
     * @param component   component that we want to access ("list, card, board, etc.").
     * @param componentId id of the component that we want to access.
     * @param url         url of the component (board url, list url, etc).
     * @return the http response.
     * @throws IOException If the request fails.
     */
    // Function for HTTP request for components
    private Response HTTPRequest(String component, String componentId, String url) throws IOException {
        //HTTP request to access
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(url + componentId + "/" + component + "?key=" + apiKey + "&token=" + apiToken).build();

        return this.httpClient.newCall(request).execute();
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
        //HTTP request to access the lists
        Response response = HTTPRequest("lists", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class List
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class List
        return mapper.readValue(response.body().string(), List[].class);
    }

    /**
     * @param listName name of the list.
     * @param boardId  board id.
     * @return the list in the board identified by the board id.
     */
    // Function to return a specific list in the board
    public List getList(String listName, String boardId) throws IOException {
        var lists = this.getBoardLists(boardId);
        for (List list : lists) {
            if (list.getName().equals(listName)) {
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
    // Function to return all the cards in the board
    public Card[] getBoardCards(String boardId) throws IOException {
        //HTTP request to access all cards in the board
        Response response = HTTPRequest("cards", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Card
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Card
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param listId id of the list.
     * @return all cards in the list identified by the list id.
     * @throws IOException If the request fails.
     */
    // Function to return all the cards in a specific list
    public Card[] getListCards(String listId) throws IOException {
        //HTTP request to access a List
        Response response = HTTPRequest("cards", listId, this.listURL);

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class List
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class List
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param cardId id of the card.
     * @return all members in the card identified by the card id.
     * @throws IOException If the request fails.
     */
    public Member[] getMemberOfCard(String cardId) throws IOException {
        //HTTP request to access all Members of a Card
        Response response = HTTPRequest("members", cardId, cardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Member
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Member
        return mapper.readValue(response.body().string(), Member[].class);
    }

    /**
     * @param sprintNumber number of the sprint.
     * @param boardId      id of the board.
     * @return an array with the start date and the end date of the specific sprint.
     * @throws IOException If the request fails.
     */
    // Function to return the start and end dates of a specific sprint
    public String[] getSprintDates(String boardId, int sprintNumber) throws IOException {
        // flag to see if we've found the start date
        boolean startDateFound = false;
        // initialize list of dates
        String[] dates = new String[2];
        String listName = "Ceremonies - Sprint " + sprintNumber;

        // get the list of all ceremonies
        var list = this.getList(listName, boardId);
        var cards = this.getListCards(list.getId());

        // Iterate over all cards in the list
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

    /**
     * @param boardId      id of the board.
     * @param sprintType   ceremony of the sprint.
     * @param sprintNumber number of the sprint.
     * @return a String with the description of the Sprint Ceremony in the specific sprint.
     * @throws IOException If the request fails.
     * @author Miguel Romana.
     */
    // Function to get the description of a Sprint Ceremony of a specific sprint
    public String getCeremonyDescription(String boardId, String sprintType, int sprintNumber) throws IOException {

        String listName = "Ceremonies - Sprint " + sprintNumber;

        // get the list of all ceremonies
        var list = this.getList(listName, boardId);
        var cards = this.getListCards(list.getId());

        // Iterate over all cards in the list
        for (Card c : cards)
            // get the Sprint sprintType's description
            if (c.name.equals("Sprint " + sprintType + " - Sprint " + sprintNumber))
                return c.getDescription();
        return ""; // returns an empty String if the description doesn't exist
    }

    /**
     * @param boardId      id of the board.
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
        var cards = this.getListCards(list.getId());
        for (Card card : cards) {
            doneItems.add(card.name);
        }
        return doneItems;
    }

    /**
     * @param boardId id of the board.
     * @return the total number of ceremonies that have been done.
     * @throws IOException If the request fails.
     */
    // function to get the total number of ceremonies
    public int getTotalNumberOfCeremonies(String boardId) throws IOException {
        int numberOfCeremonies = 0;
        var ceremoniesLists = this.getListsThatStartWith(boardId, "Ceremonies");
        for (List ceremoniesList : ceremoniesLists) {
            var ceremoniesListCards = this.getListCards(ceremoniesList.id);
            numberOfCeremonies += ceremoniesListCards.length;
        }
        return numberOfCeremonies;
    }

    /**
     * @param boardId      id of the board.
     * @param sprintNumber number of the sprint.
     * @return the total number of ceremonies that have been done.
     * @throws IOException If the request fails.
     */
    // function to get the total number of ceremonies in a specific sprint
    public int getTotalNumberOfCeremoniesPerSprint(String boardId, int sprintNumber) throws IOException {
        var lists = this.getBoardLists(boardId);
        for (List ceremoniesList : lists) {
            if (ceremoniesList.getName().equals("Ceremonies - Sprint " + sprintNumber)) {
                var ceremoniesListCards = this.getListCards(ceremoniesList.id);
                return ceremoniesListCards.length;
            }
        }
        return 0;
    }

    /**
     * @param boardId    id of the board.
     * @param startsWith id of the board.
     * @return an array of all the lists where the name starts with a specific string.
     * @throws IOException If the request fails.
     */
    public ArrayList<List> getListsThatStartWith(String boardId, String startsWith) throws IOException {
        var allLists = this.getBoardLists(boardId);
        var listsThatStartWith = new ArrayList<List>();
        for (List list : allLists) {
            if (list.getName().startsWith(startsWith)) {
                listsThatStartWith.add(list);
            }
        }
        return listsThatStartWith;
    }

    /**
     * @param boardId    id of the board.
     * @param query name present in the list name.
     * @return an array of all the lists where the name contains with a specific string.
     * @throws IOException If the request fails.
     */
    public ArrayList<List> getListsThatContain(String boardId, String query) throws IOException {
        var allLists = this.getBoardLists(boardId);
        var listsThatStartWith = new ArrayList<List>();
        for (List list : allLists) {
            if (list.getName().contains(query)) {
                listsThatStartWith.add(list);
            }
        }
        return listsThatStartWith;
    }

    /**
     * @param boardId id of the board.
     * @return the total hours spent by the team in ceremonies.
     * @throws IOException If the request fails.
     */
    public double getTotalHoursCeremony(String boardId) throws IOException {
        Pattern global = Pattern.compile("(?:@global (\\d?.?\\d+)/(\\d?.?\\d+))");
        double totalOfHours = 0;
        ArrayList<List> listOfCeremonies = this.getListsThatStartWith(boardId, "Ceremonies");
        for (List list : listOfCeremonies) {
            for (Card card : this.getListCards(list.getId())) {
                Matcher match = global.matcher(card.getDescription());
                while (match.find()) {
                    totalOfHours += Double.parseDouble(match.group(1));
                }
            }
        }
        return totalOfHours;
    }

    /**
     * Contains relevant information about the time a user spent on the project.
     */
    static class HoursPerUser {
        private String user;
        private double spentHours;
        private double estimatedHours;

        /**
         * Class to get all hours spent and estimated by user.
         *
         * @param user Member.
         * @param spentHours spent hours.
         * @param estimatedHours estimated hours.
         */
        public HoursPerUser(String user, double spentHours, double estimatedHours) {
            this.user = user;
            this.spentHours = spentHours;
            this.estimatedHours = estimatedHours;
        }

        /**
         * @return The user.
         */
        public String getUser() {
            return user;
        }

        /**
         * @return the hours that were spent.
         */
        public double getSpentHours() {
            return spentHours;
        }

        /**
         * @return the hours that were estimated.
         */
        public double getEstimatedHours() {
            return estimatedHours;
        }

        /**
         * @param hours spent hours added to the user.
         */
        private void addSpentHours(double hours) {
            this.spentHours += hours;
        }

        /**
         * @param hours estimated hours added to the user.
         */
        private void addEstimatedHours(double hours) {
            this.estimatedHours += hours;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HoursPerUser that = (HoursPerUser) o;
            return Objects.equals(user, that.user);
        }
    }
    /**
     * @param boardId id of the board.
     * @param boardQuery name present on the list name.
     * @param cardQuery name present on the card name.
     * @return the total hours spent by user in a list of {@link HoursPerUser}
     * @throws IOException If the request fails.
     */
    public ArrayList<HoursPerUser> getTotalHoursByUser(String boardId, String boardQuery, String cardQuery) throws IOException {
        Pattern global = Pattern.compile("(?:@(.+) (\\d?.?\\d+)/(\\d?.?\\d+))");

        var hoursPerUser = new ArrayList<HoursPerUser>();
        ArrayList<List> listOfCeremonies = this.getListsThatContain(boardId, boardQuery);
        for (List list : listOfCeremonies) {
            for (Card card : this.getListCards(list.getId())) {
                if (!card.getName().contains(cardQuery))
                    continue;

                for (var member : this.getMemberOfCard(card.getId())) {
                    if (!hoursPerUser.contains(new HoursPerUser(member.getName(), 0.0, 0.0))) {
                        hoursPerUser.add(new HoursPerUser(member.getName(), 0.0, 0.0));
                    }
                }

                Matcher match = global.matcher(card.getDescription());
                while(match.find()) {
                    for (var o : hoursPerUser) {
                        if (Objects.equals(o.user, match.group(1))) {
                            o.addSpentHours(Double.parseDouble(match.group(2)));
                            o.addEstimatedHours(Double.parseDouble(match.group(3)));
                        }
                    }
                }
            }
        }
        return hoursPerUser;
    }
}