import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import org.json.simple.*;
import java.util.StringTokenizer;
public class Parser {
	BigDecimal formatchanger(String param)
	{
		BigDecimal value=new BigDecimal(param);
		value=value.setScale(2,BigDecimal.ROUND_HALF_UP);
		return value;
	}
	public static void main(String[] args) throws IOException, SQLException {
		Parser parse=new Parser();
		Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Parsing","root","Password-1");
		System.out.println("Database connection successful!");
		Statement stm=conn.createStatement();
		stm.executeUpdate("create table if not exists data(transaction varchar(64),time DECIMAL(10,2));");
		System.out.println("\n Table created\n");
		File file = new File("input.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String eachline;
		int transaction=0;
		JSONArray arr=new JSONArray();
		JSONObject obj=new JSONObject();
		JSONObject obj1=new JSONObject();
		JSONObject obj2=new JSONObject();
		System.out.println("Inserting Values into the Database.. Please Wait...");
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
		}
		ResultSet rs=stm.executeQuery("select * from data");
		while(rs.next())
		{
			String key=rs.getString("transaction");
			obj.put(key,parse.formatchanger(rs.getString("time")));
		}
		obj1.put("value",obj);
		obj2.put("sampletransaction",obj1);
		rs = stm.executeQuery("select max(time) from data"); 
		if(rs.next())
		{
			obj1.put("maxcpu",parse.formatchanger(rs.getString(1)));
		}
		rs = stm.executeQuery("select avg(time) from data"); 
		if(rs.next())
		{
			obj1.put("avgcpu",parse.formatchanger(rs.getString(1)));
		}
		arr.add(0,obj2);
		FileWriter file1=new FileWriter("result.json");
		file1.write(arr.toString());
		file1.close();
		System.out.println("\nData Added Successfully!\n");
		File file3 = new File("result.json");
		BufferedReader br1 = new BufferedReader(new FileReader(file3));
		String line;
		while((line=br1.readLine())!=null)
		{
			System.out.println(line);
		}
	}
}
