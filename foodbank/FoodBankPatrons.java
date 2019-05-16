//Ibrahim Rahman
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;

public class FoodBankPatrons{
	//FoodBankPatrons will have a main method in which a FoodBank, 
	public static ReentrantLock locked;

	public static void main(String[] args){
		locked = new ReentrantLock();
		//The FoodProcuder and FoodConsumer must share the same FoodBank object.
		FoodBank foodbank = new FoodBank();
		//FoodProcuder, and FoodConsumer object are created. 
		FoodProducer producer = new FoodProducer(foodbank);
		FoodConsumer consumer = new FoodConsumer(foodbank);
		
		Thread producerthread = new Thread(producer);
		Thread consumerthread = new Thread(consumer);
		
		// Once created, the main method starts these threads. 

		producerthread.start();
		consumerthread.start();
		
	}
}