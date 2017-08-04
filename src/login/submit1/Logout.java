package login.submit1;

import java.io.IOException;

import tab.list.att_parameter;

import kissmediad2d.android.LoginActivity;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class Logout {
    /*
     * 這邊tab.java所呼叫，用來登出的
     */

    String[] tab;
    // public String homeIp ;
    // public String requestString = new String();
    public String logout_cookie;
    String homeIp, cookie;

    // retrieve retrieve = new retrieve();
    // 2014/4/2 4$ 新增 readfile_coolie,Alive

    public Logout() {
	cookie = new String();
    }

    public void logout_start() {

	try {
	    Thread thread = new Thread() {
		public void run() {

		    //先取得來自login時所紀錄下的cookie
		    //直接傳入cookie，讓server把使用者登出
		    String pathUrl = "http://" +  LoginActivity.Homeip + "/account/logout/";
		    HttpRequest request = HttpRequest.get(pathUrl);
		    String body = request.header("cookie", Login.latest_cookie).body();

		}
	    };
	    thread.start();

	} catch (HttpRequestException httpex) {
			System.out.println("你沒有網路");
	}catch (Exception ex) {
	    ex.printStackTrace();
	}

    }
}
