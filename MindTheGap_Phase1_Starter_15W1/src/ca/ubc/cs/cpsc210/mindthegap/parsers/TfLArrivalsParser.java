package ca.ubc.cs.cpsc210.mindthegap.parsers;

import ca.ubc.cs.cpsc210.mindthegap.model.Arrival;
import ca.ubc.cs.cpsc210.mindthegap.model.Line;
import ca.ubc.cs.cpsc210.mindthegap.model.Station;
import ca.ubc.cs.cpsc210.mindthegap.parsers.exception.TfLArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * A parser for the data returned by the TfL station arrivals query
 */
public class TfLArrivalsParser extends TfLAbstractParser {

    /**
     * Parse arrivals from JSON response produced by TfL query.  All parsed arrivals are
     * added to given station assuming that corresponding JSON object as all of:
     * timeToStation, platformName, lineID and one of destinationName or towards.  If
     * any of the aforementioned elements is missing, the arrival is not added to the station.
     *
     * @param stn             station to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by TfL
     * @throws JSONException  when JSON response does not have expected format
     * @throws TfLArrivalsDataMissingException  when all arrivals are missing at least one of the following:
     * <ul>
     *     <li>timeToStation</li>
     *     <li>platformName</li>
     *     <li>lineId</li>
     *     <li>destinationName and towards</li>
     * </ul>
     */
    public static void parseArrivals(Station stn, String jsonResponse)
            throws JSONException, TfLArrivalsDataMissingException {

        JSONArray array = new JSONArray(jsonResponse);

        Set<Line> acc = stn.getLines();

        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            String platName = ob.getString("platformName");
            int timeToStation = ob.getInt("timeToStation");
            String destName = ob.getString("destinationName");
            String parsedName = parseName(destName);
            String lineId = (String) ob.get("lineId");
            String lineName = (String) ob.get("lineName");

            Arrival arrival = new Arrival(timeToStation, parsedName, platName);

            for (Line line : acc)
                if (line.getId().equals(lineId))
                    stn.addArrival(line, arrival);

        }
    }
}
