package bcmBackend;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class FriendList {
	private ArrayList<Friend> friendArray = new ArrayList<Friend>();
	private File fileFriendslist;
	public static String fileDelimiter = ";";
    private String lineSeparator = System.getProperty("line.separator");
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
	
	
	public Friend searchFriends(String ipAdd){
	       for(Friend friend : friendArray){
	       	    if (friend.getIP().equals(ipAdd)) return friend;
	       }
	    return null;
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
	   
	   public void saveChanges() {
		   try {
			   saveFile("friends.txt");
		   } catch (FileNotFoundException e) {
			   e.printStackTrace();
		   } catch (IOException e) {
			   e.printStackTrace();
		   }

	   }
	   
	   
	   //TODO save FriendList to file functions
	    public void saveFile(String filename) throws FileNotFoundException, IOException {
	    	setSaveFile(filename);
	    	String stringToSave = FriendArrayToExportString();
	    	Writer dictSaver = new BufferedWriter(new FileWriter(this.fileFriendslist));

	    	try {
	    		dictSaver.write(stringToSave.toString());
	    	} finally {
	    		dictSaver.close();
	    	}

	    }
	    
	    
	    private void setSaveFile(String filename) throws IOException{
	 	   if (this.fileFriendslist == null) {
	 		   this.fileFriendslist = new File(filename);
	 	   } else if (fileFriendslist.getName().compareToIgnoreCase(filename) != 0) {
	 		   this.fileFriendslist = new File(filename);
	 	   }

	 	   if (!this.fileFriendslist.exists()) {
	 		  fileFriendslist.createNewFile();
	 	   } else if (!fileFriendslist.canWrite()) {
	 		   throw new IllegalArgumentException("File cannot be written:" + this.fileFriendslist);
	 	   }
	    }
	    
	    
	   private String FriendArrayToExportString() {
		   StringBuilder stringToSave = new StringBuilder();
		   StringBuilder sortedStringToSave = new StringBuilder();
		   String[] unsortedLines;
		   
		   for (Friend fr: friendArray) {
			   stringToSave.append(fr.getUsername()  + FriendList.fileDelimiter +
					   			   fr.getNickname()  + FriendList.fileDelimiter +
					   			   fr.getIP()		 + this.lineSeparator);
		   }
		   unsortedLines = stringToSave.toString().split(lineSeparator);
		   Arrays.sort(unsortedLines);
		   // REBUILD EXPORTABLE STRING
		   for(String line:unsortedLines) {
			   sortedStringToSave.append(line + lineSeparator);
		   }
		   
		   return sortedStringToSave.toString();
	   }
	
}
