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

    public Board[] getBoards() throws IOException {
        //HTTP request to acess every user's boards
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.baseAPIUrl).build();

        Response resp = this.httpClient.newCall(request).execute();
        // Print response url
        System.out.println(resp);

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(resp.body().string(), Board[].class);
    }

    public static void get_info(String[] user_git_info, String[] user_trello_info) throws IOException {
        TrelloApi trello = new TrelloApi(user_trello_info[0], user_trello_info[1], user_trello_info[2]);
        var boards = trello.getBoards();
        for (int i = 0; i <= boards.length; i++){
            if (boards[i].name.equals(user_trello_info[0])){
                String boardID = boards[i].id;
            }
        }
        /*
        System.out.println(a.id);
        System.out.println(a.name);
        System.out.println(a.url);
         */
    }


}




/*
public class TrelloApi extends JSONObject {
    private static String BOARD_ID = "614df1d076293f6b763c1c9c";
    private static String CARD_ID = "6161b9ba22c7ef3873833cb7";
    private static String BOARD_URL = "https://api.trello.com/1/members/me/boards?fields=name";
    private static String CARD_URL = "https://api.trello.com/1/members/me/boards?fields=name";
    private static String CARD_IN_BOARD_URL = "https://api.trello.com/1/boards/" + BOARD_ID + "/cards/" + CARD_ID;

    public static void get_info(String[] user_git_info, String[] user_trello_info) throws UnirestException {

        HttpResponse<JsonNode> response;
        response = Unirest.get(BOARD_URL + "&key=" + user_trello_info[1] + "&token=" + user_trello_info[2])
                .header("Accept", "application/json")
                .asJson();

        JSONArray array = response.getBody().getArray();

        if (check_more_than_one_board(array)){
            // make prompt to know what board to access
            String prompt = "ES-LETI-1Sem-2021-Grupo10";
            // based on the prompt, get the id for the specific board
            String id = getBoardId(array, prompt);
            //http response to get the board that we want
            HttpResponse<JsonNode> response_board = Unirest.get("https://api.trello.com/1//boards/" +
                            id + "?"+ "&key=" + user_trello_info[1] + "&token=" + user_trello_info[2])
                    .header("Accept", "application/json")
                    .asJson();

            // print all the board information
            JSONArray board_array = response_board.getBody().getArray();
            System.out.println(board_array);
        }
    }

    // check if there is more than 1 board
    public static boolean check_more_than_one_board(JSONArray array){
        int count = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            System.out.println(object);
            // if the there is a key "name" in the array, that means that it's a board
            if (object.getString("name").equals(object.get("name"))){
                // for every board we count ++1
                count++;
            }
        }
        // if there is more than 1 board, return true
        return count > 1;
    }

    // based on a prompt, get the ID for the board that the user specified
    public static String getBoardId(JSONArray array, String prompt) {
        String board_id;
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.get("name").equals(prompt)) {
                // get the id from the board name that we wanted
                board_id = (String) object.get("id");
                return board_id;
            }
        }
        // if there is no suh name, mus be error!
        //TODO: Try to figure out how to return an error and then display message in the UI
        return "0";
    }
}
 */