package bcmNetworking;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class FriendList {
	private ArrayList<Friend> friendArray = new ArrayList<Friend>();
	private File fileFriendslist;
	public static String fileDelimiter = ";";
	public static String ipDelimiter = ".";
	
	
	public ArrayList<Friend> getList(){
		return friendArray;
	}
	
	public Friend addFriend(String un, String nick, String ip){
		Friend currentFriend = new Friend(un, nick, ip);
		friendArray.add(currentFriend);
		return currentFriend;
	}
	
	
	//CONSTRUCTOR
	public FriendList(){
		loadFile("friends.txt");
	}
	
	
	
	
	private void loadFile(String filename){
		   this.fileFriendslist = new File(filename);

		   try {
			   processFile();
		   } catch(FileNotFoundException e){
			   System.out.println("File not found.");
		   }
		   
	   }

	   private void processFile() throws FileNotFoundException {
		   Scanner linescanner = new Scanner(new FileReader(this.fileFriendslist));

		   try {
			   while ( linescanner.hasNextLine() ){
				   parseLine( linescanner.nextLine() );
			   }
		   }
		   finally {
			   linescanner.close();
		   }

	   }


	   private void parseLine(String lineFromFile) {
		   String[] friendField = lineFromFile.split(fileDelimiter);

		   if (friendField.length >= 3) {
			   this.addFriend (friendField[0].trim(),	// username
					   		   friendField[1].trim(),   // nickname
					   		   friendField[2].trim());  // ipaddress
		   }

	   }
	   
	   
	   
	   //TODO save FriendList to file functions
	
}
