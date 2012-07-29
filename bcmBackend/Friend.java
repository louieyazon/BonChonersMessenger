package bcmBackend;
public class Friend{
		private String username;
		private String nickname;
		private String ipaddress; 
		
		private boolean online;
		private boolean typing;
		
		
		public Friend(String un, String nick, String ip) {
			this.setUsername(un);
			this.setNickname(nick);	
			this.setIP(ip);
		}
		
		public void setIP(String ip){
			this.ipaddress = ip;
		}
		

		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public boolean isOnline() {
			return online;
		}
		public String getIP(){
			return this.ipaddress;
		}
		public void setOnline(boolean online) {
			this.online = online;
		}
		public boolean isTyping() {
			return typing;
		}
		public void setTyping(boolean typing) {
			this.typing = typing;
		}
		
		public boolean isEmpty() {
			return (   this.ipaddress.trim().equals("")
					&& this.nickname.trim().equals("")
					&& this.username.trim().equals(""));
			
		}
		
		
		
		
		
	}