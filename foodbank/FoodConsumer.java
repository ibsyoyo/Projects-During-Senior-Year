import java.lang.Thread;
import java.util.Random;

public class FoodConsumer extends Thread{
	
	public FoodBank bank = new FoodBank();
	
	public FoodConsumer(FoodBank fbank){
		bank = fbank;
	}
	
	@Override
	public void run(){
		try {
			while(true){
				//Both actions of giving and taking food will be monitors and will involve locking the lock object and unlocking it when done. 
				FoodBankPatrons.locked.lock();
				Random r = new Random();
				int i = r.nextInt(100) + 1;
				//FoodConsumer is identical to FoodProducer except that the random number generated 
				//in run will be removed from the FoodBank object. 
				bank.takeFood(i);			
				FoodBankPatrons.locked.unlock();
				Thread.sleep(100);

			}
			}
		catch(Exception e){
			System.out.println("third exception occured");
		}
		
			
		}
		
	}

