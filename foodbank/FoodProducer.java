import java.lang.Thread;
import java.util.Random;

//FoodProducer will extend the Thread class 
public class FoodProducer extends Thread{
	//FoodProducer will have a single instance variable named bank of type FoodBank. 
	public FoodBank bank;
	
	//FoodProducer will have a parameterized constructor with a single parameter 
	//of type FoodBank.
	public FoodProducer(FoodBank fbank){
		//The parameterized constructor will initialize the value of 
		//bank to the single parameter.
		bank = fbank;
	}
	
	//FoodProducer’s run method will loop infinitely. 
	//override Thread’s run method.
	@Override
	public void run(){
		//System.out.print("test");
		try {
		while(true){
			//Both actions of giving and taking food will be monitors and will involve locking the lock object and unlocking it when done. 
			FoodBankPatrons.locked.lock();
			//On each loop iteration run will generate a random number from 1-100 and add 
			//that much food to the bank instance variable. 
			Random r = new Random();
			int i = r.nextInt(100) + 1;
			
			bank.giveFood(i);
			FoodBankPatrons.locked.unlock();

			//After adding food, the thread will sleep for 100 milliseconds. 
				Thread.sleep(100);
			}
		}catch(Exception e){
				System.out.println("second exception occured");
			}
		}
	}
	
