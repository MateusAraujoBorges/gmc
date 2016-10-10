package edu.udel.cis.vsl.gmc.concurrent.util;

import java.util.ArrayList;
import java.util.List;

import edu.udel.cis.vsl.gmc.simplemc.State;
import edu.udel.cis.vsl.gmc.simplemc.Transition;

public class Log {
	public static List<Transaction> transactions =  new ArrayList<Transaction>();
	
	public static synchronized void add(Transaction t){
		transactions.add(t);
	}
	
	public static void analyzeLog(){
		int size = transactions.size();
		
		for(int i=0; i<size; i++){
			Transaction transaction = transactions.get(i);
			int transactionType = transaction.getTransactionType();
			Transition transition = transaction.getTransition();
			State sourceState = transaction.getSourceState();
			State desState = transaction.getDesState();
			int id = transaction.getThreadId();
			int temp = id;
			while(temp > 0){
				System.out.print("                              ");
				temp--;
			}
			String outPut = "";
			switch (transactionType){
				case 1: 
				{	
					
					// spawn new thread
					outPut = "thread" + id + " spaws new thread ("
						+ sourceState.toString() + "__" + transition.getId() + "-->"
						+ desState.toString() + ")";
					break;
				}
				case 2:
				{
//					System.out.println(desState);
					// proceed depth first search
					outPut = "thread" + id + ":"
							+ sourceState.toString() + "__" + transition.getId() + "-->"
							+ desState.toString();
					break;
				}
				case 3:
					// check stack proviso
					outPut = "thread" + id + " checks stack proviso ("
							+ sourceState.toString() + "__" + transition.getId() + "-->"
							+ desState.toString() + ")";
					break;
				default:
					outPut = "undefined behavior!";
			}
			System.out.println(outPut);
		}
	}
}
