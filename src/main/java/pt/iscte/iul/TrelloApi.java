package pt.iscte.iul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class TrelloApi extends JSONObject {
    private static String BOARD_ID = "614df1d076293f6b763c1c9c";
    private static String CARD_ID = "6161b9ba22c7ef3873833cb7";
    private static String BOARD_URL = "https://api.trello.com/1/members/me/boards?fields=name";
    private static String CARD_URL = "https://api.trello.com/1/members/me/boards?fields=name";
    private static String CARD_IN_BOARD_URL = "https://api.trello.com/1/boards/" + BOARD_ID + "/cards/" + CARD_ID;

    public static void get_info(String[] user_git_info, String[] user_trello_info) throws UnirestException {

        HttpResponse<JsonNode> response;
        response = Unirest.get(CARD_IN_BOARD_URL + "?key=" + user_trello_info[1] + "&token=" + user_trello_info[2])
                .header("Accept", "application/json")
                .asJson();

        JSONArray array = response.getBody().getArray();

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            System.out.println(object);
        }
    }
}
