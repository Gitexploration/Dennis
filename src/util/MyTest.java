package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MyTest {
	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		for(int i =0;i<3;i++){
			c.add(Calendar.DAY_OF_YEAR, -1);
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr1 = sdf1.format(c.getTime());
			System.out.println(dateStr1);
			String[] str = dateStr1.split("-");
			String year =str[0];
			String month=str[1];
			String day =str[2];
			System.out.println(year+","+month+","+day);
			
		}
		
		
	}
	


}
