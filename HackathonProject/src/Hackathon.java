import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

public class Hackathon {

	
	//Change Data to 2 Decimal Format
	BigDecimal formatchanger(String param)
	{
		BigDecimal value=new BigDecimal(param);
		value=value.setScale(2,BigDecimal.ROUND_HALF_UP);
		return value;
	}
	
	
	//Pushing Max and Average Value to Database
	void databaseConnectivity(String usecase, Double max,Double avg) throws SQLException
	{
		Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/memorydata","root","Password-1");		
		System.out.println("\nDatabase connection successful!");
		Statement stm=conn.createStatement();
		stm.executeUpdate("create table if not exists data(Usecasename varchar(40),maxmemory DECIMAL(10,2),avgmemory DECIMAL(10,2));");
		System.out.println("\n Table created\n");
		stm.executeUpdate("insert into data(Usecasename,maxmemory,avgmemory)values('"+usecase+"',"+max+","+avg+");");
		System.out.println("Data Inserted into Database!");
	}
	
	//Reading Values from text file and parsing to JSONObject
	void tokenize() throws Exception 
	{
		String eachline;
		int linecount=0,transaction=0;
		Double sum=0.00,max=0.0,value=0.0,avg;
		BufferedReader br=new BufferedReader(new FileReader(new File("Memory.txt")));
		JSONObject memory=new JSONObject();
		JSONObject jsonvalues=new JSONObject();
		while((eachline=br.readLine())!=null)
		{
			linecount=linecount+1;
			if(linecount%2==0)				//Choosing only even line number to read values
			{
				transaction=transaction+1;
				StringTokenizer st=new StringTokenizer(eachline," ");
				st.nextToken();				//To Eliminate first token -TOTAL 
				value=Double.parseDouble(st.nextToken())/1024;		//fetching value and converting KB to MB
				jsonvalues.put(transaction+"s",formatchanger(value.toString()));
				sum+=value;					//calculate sum value 
			}
			max=max<value?value:max;		//finding maximum value
		}
		avg=sum/transaction;				//finding average value
		memory.put("values",jsonvalues);
		memory.put("Usecasename","Sample");
		memory.put("AverageMemory(MB)",formatchanger(avg.toString()));
		memory.put("MaxMemory(MB)",formatchanger(max.toString()));
		
		String usecase="Sample";
		JsonFileCreation(memory);//Writing JSONObject to File
		databaseConnectivity(usecase,max, avg);//Wring Values to DataBase
		br.close();
	}
	
	//Creating JsonFile 
	void JsonFileCreation(JSONObject jsob) throws IOException
	{
		FileWriter file1=new FileWriter("result.json");
		file1.write(jsob.toString());
		System.out.println("Parsed to JsonFile Successfully!\n");
		file1.close();
		System.out.println(jsob+"\n");
	}
	
	public static void main(String[] args) throws Exception {
		Hackathon hackobj=new Hackathon();
		hackobj.tokenize();
	}

}
