package in.technolabs.googlecoordinates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = {"/", "/google"}, method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome Google Co-ordinates! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		logger.info("Request Received @ {}.", formattedDate);
		
		return "home";
	}
	
	
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/google-calc", method = RequestMethod.POST)
	public void googleCalc(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, IOException {
		Map<Double, Double> resultMap = new HashMap<Double, Double>();
		
		double distance  =  Double.parseDouble(request.getParameter("distance"));
		String country = request.getParameter("country");
		String location = request.getParameter("location");
		
		String northWest =  request.getParameter("nw-cordinate");
//		String northEast =  request.getParameter("ne-cordinate");
		String southWest =  request.getParameter("sw-cordinate");
		String southEast =  request.getParameter("se-cordinate");
		
		
		double northWestLat = Double.parseDouble(northWest.split(",")[0].trim());
		double southWestLat = Double.parseDouble(southWest.split(",")[0].trim());
		double northWestLng = Double.parseDouble(northWest.split(",")[1].trim());
		double southEastLng = Double.parseDouble(southEast.split(",")[1].trim());
		
		Map<Double, Double> tempMap = new HashMap<Double, Double>(); 
		List<Double> temp = null;
		
		double northWestLatTemp = northWestLat;
		double northWestLngTemp = northWestLng;
		do {
			double radianV = 180;
			temp = calculateCordinates(distance, northWestLatTemp, northWestLngTemp, radianV);
			tempMap.put(temp.get(0), temp.get(1));
			northWestLatTemp = temp.get(0);
			northWestLngTemp = temp.get(1);
		} while (temp.get(0) > southWestLat);
		
		resultMap.putAll(tempMap);
		
		Iterator<Entry<Double,Double>> iterator = tempMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Double,Double> entry = (Map.Entry<Double,Double>) iterator.next();
			
			double toSouthEastLatTemp = entry.getKey();
			double toSouthEastLngTemp = entry.getValue();
			do {
				double radianH = 90;
				temp = calculateCordinates(distance, toSouthEastLatTemp, toSouthEastLngTemp, radianH);
				toSouthEastLatTemp = temp.get(0);
				toSouthEastLngTemp = temp.get(1);
				resultMap.put(temp.get(0), temp.get(1));
			} while (temp.get(1) < southEastLng);
		} 
		
		File file = ResourceUtils.getFile("classpath:co-ordinates.json");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode cordinateNode =  objectMapper.createObjectNode();

		ObjectNode jsonNode =  objectMapper.createObjectNode();
		jsonNode.put("cordinatesby", "http://technolabs.in");
		jsonNode.put("distance", distance);
		jsonNode.put("country", country);
		jsonNode.put("location", location.toUpperCase());
		
		long count = 0 ;
		Iterator<Entry<Double,Double>> it = resultMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double,Double> entry = (Map.Entry<Double,Double>) it.next();
			cordinateNode.put(String.valueOf(count), entry.getKey()+","+entry.getValue());
			count++;
		}
		jsonNode.put("co-ordinates", cordinateNode);
		FileUtils.write(file, String.valueOf(jsonNode));
		FileInputStream inputStream = new FileInputStream(file);
		
		String mimeType = "application/octet-stream";
		response.setContentType(mimeType);
        response.setContentLength((int) file.length());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", country+"_"+location+".json");
        response.setHeader(headerKey, headerValue);
 
        // get output stream of the response
        OutputStream outStream = response.getOutputStream();
        int BUFFER_SIZE = 4096;
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
 
        inputStream.close();
        outStream.close();		
	}
	
	
	private List<Double> calculateCordinates(double distance, double lat, double lng, double radian){
		List<Double> result = null;		
		
		double dist = distance/ 6371.0;
		double brng = Math.toRadians(radian);
		double lat1 = Math.toRadians(lat);
		double lon1 = Math.toRadians(lng);
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
		double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
		double lon2 = lon1 + a;
		lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
		result = new ArrayList<Double>();
		result.add(0, Math.toDegrees(lat2));
		result.add(1, Math.toDegrees(lon2));
		return result;
	}
	
}
