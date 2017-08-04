package database.storage;

import java.util.HashMap;


public class statecode {
	
	public static HashMap<Integer, String> transcript;
	statecode(){
		transcript = new HashMap<Integer, String>();
		transcript.put(100, "錯誤的HTTP方法");
		transcript.put(101, "傳入參數錯誤");
		transcript.put(102, "使用者名稱或密碼錯誤");
		transcript.put(103, "使用者名稱尚未驗證");
		transcript.put(104, "帳號尚未激活");
		transcript.put(105, "使用者錯誤");
		transcript.put(106, "使用者名稱已存在");
		transcript.put(107, "驗證碼錯誤");
		transcript.put(108, "驗證碼逾期");
		transcript.put(109, "檔案識別碼錯誤");
		transcript.put(110, "收件者不存在");
		transcript.put(111, "電話號碼不存在");
		transcript.put(112, "簡訊通知失敗");
		transcript.put(113, "內容不可為空");
		transcript.put(114, "訊息不存在");
		transcript.put(115, "檔案序號錯誤");
		transcript.put(116, "檔案尚未上傳");
		
	}
	
	
}