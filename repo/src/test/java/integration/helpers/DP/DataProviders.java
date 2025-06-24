package integration.helpers.DP;

import cl.bice.robotpro.services.dataprovider.DataProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Map;

public class DataProviders {

	public static JsonArray getFromDP(String dataProviderId) {
		Map<String, Object> bateryData = DataProvider.getFromDp(dataProviderId);
		Gson gson = new Gson();
		if ((bateryData!= null)){
			Object data = bateryData.get("data");
			if (!(data== null)) {
				JsonElement jsonElement = gson.toJsonTree(data);
				return jsonElement.getAsJsonArray();
			}
		}
		return null;
	}
}