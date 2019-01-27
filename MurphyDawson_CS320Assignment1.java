import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
import java.util.*;

//Dawson Murphy
//This program provides the user with the bus route stops schedule for a 
//particular bus route number in a given schedule at Community Transit. It allows the 
//user to enter a letter, then lists all the cities whose first initial are that letter,
//and the buses that serve those cities. It then allows the user to enter a bus route number,
//and prints all that buses stops.
public class MurphyDawson_CS320Assignment1 {
	public static void main(String[] args) throws Exception {
		String htmlString=getData("https://www.communitytransit.org/busservice/schedules/");
		Scanner console=new Scanner(System.in); //Takes user input
		char b=enterInitial(console);
		listStopsAndRoutes(htmlString, b);
		String busLink=findBusLink(console,htmlString);
		if(!busLink.equals("")){ //Only attempts to find the busses stops if the bus number is valid
			String busData=getData(busLink);
			listStops(busData);
		}
	}
	//Uses a provided URL to load that pages HTML data, and returns it as a string.
	public static String getData(String url) throws Exception{
		URLConnection bc = new URL(url).openConnection();
		bc.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader in=new BufferedReader(new InputStreamReader(bc.getInputStream()));
		String inputLine="";
		String htmlData="";
		//Iterates through the pages HTML and adds the lines to the string htmlData
		while ((inputLine = in.readLine()) != null) {
			htmlData+= inputLine + "\n";
		}
		in.close();
		return htmlData;
	}
	//Takes in a string representing htmlData and a character, and searches for 
	//cities whose first letter match that character. It then prints the cities names and 
	//the bus numbers that serve them.
	public static void listStopsAndRoutes(String htmlString,char c){
		c=java.lang.Character.toUpperCase(c); 
		//Pattern to search the htmlString, finding the city name that matches 
		//and then grabs a large chunk of text to later search for bus numbers
		Pattern pattern1 = Pattern.compile("<h3>("+c+".*?)</h3>(.*?)<hr id", Pattern.DOTALL);
		Matcher matcher1 = pattern1.matcher(htmlString);
		//Pattern for searching the chunk of text after the city name for the bus numbers that serve it
		Pattern pattern2 = Pattern.compile("<strong><a href=\"/schedules/route/.*>(\\S*)</a></strong>");
		while(matcher1.find()) { //While the matcher finds matches
			System.out.println("Destination: "+matcher1.group(1)); //Prints the city name
			String match2=matcher1.group(2);
			Matcher matcher2 = pattern2.matcher(match2); //Searches the second match for bus numbers
			while(matcher2.find()){ //When bus numbers found
				System.out.println("Bus Number: "+matcher2.group(1)); //Prints the bus numbers
			}
		System.out.println("++++++++++++++++++++++++++++++++++++");//Seperates the cities
		}
	}
	//Takes in html data as a string, the html data being for a bus numbers route,
	//and searches it for the names of the buses stops
	public static void listStops(String busData){
		//Pattern to find the destination names, and a chunk of text to search through for stop
		//on the way to that destination
		Pattern pattern1 = Pattern.compile("<h2>Weekday<small>(.*?)</small></h2>(.*?)</tr>\\s*</tbody>\\s*</table>", Pattern.DOTALL);
		Matcher matcher1 = pattern1.matcher(busData);
		//Pattern for searching the chunk of text for stop names
		Pattern pattern2 = Pattern.compile("fa-stack-1x\">(.)</strong>\\s*</span>\\s*<p>(.*)</p>");
		while(matcher1.find()) { //While the matcher finds destinations
			String match1=matcher1.group(1);
			System.out.print("Destination: "); //Prints destination name
			System.out.println(match1);
			String match2=matcher1.group(2);
			Matcher matcher2 = pattern2.matcher(match2);
			while(matcher2.find()){ //While the matcher finds stop names
				String stop=matcher2.group(2);
				String stop2="";
				//loops through the stop names searching for &, and if it finds it
				//removes "amp;" from the stop name.
				for(int i=0;i<stop.length();i++){
					if(stop.charAt(i)=='&'){
						stop2=stop.substring(0, i+1)+stop.substring(i+5,stop.length());
					}
				}
				if(!stop2.equals("")){//In case the name did not contain &
					stop=stop2;
				}
				System.out.println("Stop Number: "+matcher2.group(1)+" is "+stop); //Prints the stops name
			}
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");//Seperates the destinations
		}
	}
	//Makes sure the user is entering a valid letter for the city name. 
	//Only accepts a-z and A-Z. Loops until valid input entered.
	public static char enterInitial(Scanner console){
		System.out.print("Enter the first letter of your destination name:");
		String input = console.nextLine();
		while(!input.matches("[A-Za-z]{1}")){
			System.out.println("That is not a valid input");
			input=console.next();
		}
		char b=input.charAt(0);
		return b;
	}
	//Takes a bus route number from the user, and then searches a provided html string
	//for that bus route number. If found, prints the link for that buses schedule.
	public static String findBusLink(Scanner console, String htmlString) throws Exception{
		System.out.print("Please enter a route number: ");
		String input=console.next();
		StringBuilder bus=new StringBuilder(input);
		//If the bus route number includes '/', changes it to '-' in order to search the string
		for(int i=0;i<bus.length();i++){
			if(bus.charAt(i)=='/'){
				bus.setCharAt(i, '-');
			}
		}
		//Pattern for searching for the route number
		Pattern pattern=Pattern.compile("<strong><a href=\"(.*"+bus+")\"");
		Matcher matcher=pattern.matcher(htmlString);
		String r="";
		if(matcher.find()) {//If the matcher finds the bus route number, saves the link to its schedule as r
			URL u=new URL("https://www.communitytransit.org/busservice"+matcher.group(1));
			r=("https://www.communitytransit.org/busservice"+matcher.group(1));
			System.out.println("The link for your route is "+u); //Prints the route link
		}else{
			System.out.println("Could not find a bus route matching that input"); //Tells user if bus route number not found
		}
		return r; //Returns the route link for later use.
	}
}
