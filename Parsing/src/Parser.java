import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mysql.cj.xdevapi.JsonArray;

import java.util.StringTokenizer;

public class Parser {

	public static void main(String[] args) throws IOException, SQLException {
		DecimalFormat df = new DecimalFormat("#.##");
		Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Parsing","root","Password-1");
		System.out.println("Database connection successful!\n");
		Statement stm=conn.createStatement();
		stm.executeUpdate("create table data(transaction varchar(64),time DECIMAL(10,2));");
		System.out.println("\n Table created");
		File file = new File("input.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String eachline;
		int transaction=0;
		JsonArray obj1=new JsonArray();
		JSONObject obj=new JSONObject();
		while((eachline=br.readLine())!=null)
		{	
			transaction++;
			StringTokenizer st=new StringTokenizer(eachline," ");
			int i=1;
			while(i<9)
			{
				i++;
				st.nextToken();
			}
			stm.executeUpdate("insert into data(transaction,time)values('"+transaction+"s',"+st.nextToken()+");");	
			System.out.println("Inserting..");
		}
		ResultSet rs=stm.executeQuery("select * from data");
		while(rs.next())
		{
			String key=rs.getString("transaction");
			float val=Float.parseFloat(df.format(rs.getFloat("time")));
			obj.put(key,val);
		}
		FileWriter file1=new FileWriter("result.json");
		file1.write(obj.toString());
		file1.close();
		System.out.println("Data Added Successfully!");
	}
}
