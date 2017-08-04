package login.submit1;

import java.io.IOException;

import kissmediad2d.android.LoginActivity;

import com.github.kevinsawicki.http.HttpRequest;

public class user {
	String requestString;
    /*
     * edit.java會呼叫此class，目的取得user的名稱
     */
	public void setrequestString(String arg) {
		requestString = arg;
	}
	public void getrequestString(String arg) {
		requestString = arg;
	}
    public String[] getuser() {
	String[] userdata = new String[4];
	try {
	    String pathUrl1 = "http://" + LoginActivity.Homeip + "/account/user/";
	    HttpRequest request1 = HttpRequest.get(pathUrl1);

	    String response1 = request1.header("cookie", Login.latest_cookie).body();
	    //回傳值為ret= &user_name&user_phone&user_sms
	    userdata = response1.split("&");
	} catch (Exception ex) {

	    ex.printStackTrace();
	}
	return userdata;
    }
    public String delete_msg(String token) {
    	String res = "";
    	try {
    		System.out.println(token);
    		//2015/08/26 loginActivity改成Loginput
    	    String pathUrl1 = "http://" + LoginActivity.Homeip + "/cms/del_msg/";
    	    HttpRequest request1 = HttpRequest.post(pathUrl1);

    	    res = request1.header("cookie", Login.latest_cookie).send("token="+token).body();
    	    System.out.println("成功了唷 "+res);
    	    //回傳值為ret= &user_name&user_phone&user_sms
    	    System.out.printf("servicetime"+res);
    	} catch (Exception ex) {

    	    ex.printStackTrace();
    	}
    	return res;
    }
    public String setservicetime(String requestString) {
	String res = "";
	try {
	    String pathUrl1 = "http://" + LoginActivity.Homeip + "/cms/set_time/";
	    HttpRequest request1 = HttpRequest.post(pathUrl1);

	    res = request1.header("cookie", Login.latest_cookie).send(requestString).body();
	    //回傳值為ret= &user_name&user_phone&user_sms
	    System.out.println("servicetime"+res);
	} catch (Exception ex) {

	    ex.printStackTrace();
	}
	return res;
    }
    
    public String setd2d(String token, int urgent) {
	String res = "";
	try {
		System.out.println(token);
	    String pathUrl1 = "http://" + LoginActivity.Homeip + "/cms/set_d2d/";
	    HttpRequest request1 = HttpRequest.post(pathUrl1);

	    res = request1.header("cookie", Login.latest_cookie).send("token="+token+"&urgent="+urgent).body();
	    System.out.println("成功了唷 "+res);
	    //回傳值為ret= &user_name&user_phone&user_sms
	    System.out.printf("servicetime"+res);
	} catch (Exception ex) {

	    ex.printStackTrace();
	}
	return res;
    }
    
    public String[] getservicetime(String user) {
	String []res = null ;
	try {
		
	    String pathUrl1 = "http://" + LoginActivity.Homeip + "/cms/get_time/?user="+user;
	    HttpRequest request1 = HttpRequest.get(pathUrl1);

	    String resp = request1.header("cookie", Login.latest_cookie).body();
	    //回傳值為ret= &user_name&user_phone&user_sms
	    System.out.printf("servicetime"+resp);
	    res=resp.split("&");
	    System.out.println("d2d_time"+res);
	} catch (Exception ex) {

	    ex.printStackTrace();
	}
	return res;
    }
}