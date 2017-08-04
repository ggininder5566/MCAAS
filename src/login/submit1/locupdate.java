package login.submit1;

import kissmediad2d.android.logininput;

import com.github.kevinsawicki.http.HttpRequest;

public class locupdate {

	Alive alive = new Alive();
	Login login = new Login();
	public locupdate(){
		
	}
	public String[] locationupdate(String cookie,String ip, int port) {
		String[] aliveinfo = new String[5];
		aliveinfo = alive.alive(cookie, ip ,port);
		
		if(aliveinfo[1]!=null && aliveinfo[1].equalsIgnoreCase("true")){
			if (aliveinfo[3].equals("false")) {
				String requestString = logininput.login_name;
				aliveinfo[3] = login.login(aliveinfo[0], requestString)[0];
			}
		}else{
			
		}

		return aliveinfo;
	}
	public String []login(String aliveIp, String requestString){
		String[] loginreturn = new String[3];
		loginreturn = login.login(aliveIp, requestString);
		return loginreturn;
	}
	public String getip(String username) {
		String ip="";
		try {

			String pathUrl = "http://" + logininput.Homeip + "/cms/getip/?user="+username;
			HttpRequest request = HttpRequest.get(pathUrl);
			// 這邊requestString是subject、content、receiver、filecnt、length、file_name(至少一個)
			ip = request.header("cookie", Login.latest_cookie).body();
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ip;
		
	}
}