package ffmpeg.process;

import java.io.File;


import kissmediad2d.android.VideoTrimmingService;
import tab.list.FileUtils;
import tab.list.att_parameter;
import tab.list.fileContentProvider.UserSchema;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ffmpeg_service {
	String ffmpeg_path;
	int split_seq=0;
	int index=0;
	int total_time=0,duration;
    String selfid;
	FileUtils oname = new FileUtils();
	String outFileName;
	File file;

		public void ffmpeg(Activity a,ContentResolver content,String file_path, int start, int duration,String selfid,Float total_time,int seq) {
			att_parameter.selfid=selfid;
			file = new File(file_path);
			String inputFileName = file_path;
			ffmpeg_path = file_path;
			split_seq=seq;
			outFileName = oname.getTargetFileName(file_path, split_seq, index);
			final String[] temp_up = {UserSchema._ID};
			//紀錄最後一筆切到哪裡
			Cursor ffmpeg_exit = content.query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), temp_up, "selfid='"+selfid +"'", null, null);
			if(ffmpeg_exit.getCount()>0){
				//更新資料
				ffmpeg_exit.moveToFirst();
				ContentValues values = new ContentValues();
				values.put(UserSchema._FILEPATH, file_path);
				values.put(UserSchema._DURATION, duration);
				values.put(UserSchema._TOTAL_TIME, total_time);
				values.put(UserSchema._SELFID, selfid);
				values.put(UserSchema._SEQ_ID, split_seq);
				int id_this = Integer.parseInt(ffmpeg_exit.getString(0));
				String file_where = UserSchema._ID + " = " + id_this;
				content.update(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), values, file_where, null);
			}else{
				//新增資料
				ContentValues values = new ContentValues();
				values.put(UserSchema._FILEPATH, file_path);
				values.put(UserSchema._DURATION, duration);
				values.put(UserSchema._TOTAL_TIME, total_time);
				values.put(UserSchema._SELFID, selfid);
				values.put(UserSchema._SEQ_ID, split_seq);
				//還要加自訂ID(跟SERVER)
				content.insert(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), values);
				values=null;
			}

			
			
			File ch_file = new File(outFileName);
			// 先檢查該影片在過去是否有被切割過，如果這一塊被切割過就跳過不切
			if (ch_file.exists()) {
				Cursor file_exit = content.query(Uri.parse("content://tab.list.d2d/temp_file"), null, "selfid='"+selfid +"' and filerecord ='"+oname.getindex()+"'", null, null);
				if(file_exit.getCount()>0){
					//do nothing
				}else{
					ContentValues values = new ContentValues();
					values = new ContentValues();
					values.put(UserSchema._FILEPATH, outFileName);
					values.put(UserSchema._FILERECORD, oname.getindex());
					values.put(UserSchema._FILECHECK, 0);
					values.put(UserSchema._SELFID, selfid);
					values.put(UserSchema._FILENAME, file.getName());
					//還要加自訂ID(跟SERVER)
					content.insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
				
				}

				split_seq++;
				if(split_seq==1){
					//dialog.dismiss();
					att_parameter.first=true;
				}else{
					
				}
				if (split_seq < (total_time / duration)) {
					ffmpeg(a,content, file_path, split_seq * duration, duration,selfid,total_time,split_seq);

				} else {
					Log.i("COPYofWIRTE", "有值行到這邊唷(曾經切過)");
					//刪除切割紀錄(用ID抓)
					split_seq = 0;
					String token,content_where,file_where,ffmpeg_where = null;
					String[] Form = { UserSchema._ID,UserSchema._MESSAGETOKEN ,};
					Cursor token_cursor = content.query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='"+selfid+"'", null, null);
					if (token_cursor.getCount() > 0) {
						int id_this1 = 0;
						token_cursor.moveToFirst();
						id_this1 = Integer.valueOf(token_cursor.getString(0));
						token=token_cursor.getString(1);
						ContentValues values1 = new ContentValues();
						values1.put(UserSchema._FINISH, "yes");
						String where1 = UserSchema._ID + " = " + id_this1;
						content.update(Uri.parse("content://tab.list.d2d/temp_content"), values1, where1, null);
						//第一塊上傳完  更新FIRST為TRUE
						
						Cursor uptoken_cursor = content.query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='"+selfid+"'", null, null);
						if (uptoken_cursor.getCount() > 0) {
							ContentValues values = new ContentValues();
							 values = new ContentValues();
							uptoken_cursor.moveToFirst();
							values.put(UserSchema._MESSAGETOKEN, token);
							for (int i = 0; i < uptoken_cursor.getCount(); i++) {
								int id_this = Integer.parseInt(uptoken_cursor.getString(0));
								file_where = UserSchema._ID + " = " + id_this;
								content.update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
								uptoken_cursor.moveToNext();
							}
						}
						uptoken_cursor.close();
						
						Cursor ffmpeg_id = content.query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), new String[] { UserSchema._ID } , "selfid='"+selfid+"'", null, null);
						if (ffmpeg_id.getCount() > 0) {
							ffmpeg_id.moveToFirst();
							ffmpeg_where =ffmpeg_id.getString(0);						
						}
						ffmpeg_id.close();
						att_parameter.selfid="";
						att_parameter.ffmpeg_now=false;
						int id_this =Integer.parseInt(ffmpeg_where);
						String where = UserSchema._ID + " = " +  id_this;
						content.delete(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), where, null);
					}
					token_cursor.close();
					

				}
			} else {
				// 傳入四個參數給FFMPEG
				Bundle bundle = new Bundle();
				bundle.putString("inputFileName", inputFileName);
				bundle.putString("outFileName", outFileName);
				bundle.putInt("start", start);
				bundle.putInt("duration", duration);
				bundle.putInt("split_seq", split_seq);
				bundle.putInt("index", index);
				bundle.putFloat("total_time", total_time);
				bundle.putString("selfid", selfid);
				Intent intent = new Intent(a, VideoTrimmingService.class);
				intent.putExtras(bundle);
				a.startService(intent);
			}

		}




}