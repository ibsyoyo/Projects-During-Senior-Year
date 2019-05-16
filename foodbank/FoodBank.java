public class FoodBank{
	//FoodBank will have a single instance variable named food of type int. 

	public int food;
	
	public FoodBank(){
		//FoodBank will define a default constructor which initializes food to zero. 

		food = 0;
	}
	//FoodBank will have two methods: giveFood and takeFood. Both methods will have a single parameter of type int.
	public synchronized void giveFood(int i){
		//giveFood will add the value of the parameter to the food instance variable
		food += i;
		System.out.println("Giving " +i+ " items of food," + " the FoodBank balance is now " + food + " items. \n");

		
		try{
			notifyAll();
		}
		catch(Exception e){
			System.out.println("couldn't notify thread");
		}
	}
	
	public synchronized void takeFood(int i){
		// takeFood will subtract the value. if there is enough food. if there isnt... we dont give out any food. we want to restock.

		 if(i > food) {
			System.out.println("We have to wait to restock more food, for you to take "+i+" items. \nPlease come again soon. FoodBank balance is "+food+" items, at the moment.\n");
		}
		else {
			food -= i;
			System.out.println("Taking "+i+" items of food, the FoodBank balance is now "+food+" items.\n");
		}
		 
		 try{
				notifyAll();
			}
			catch(Exception e){
				System.out.println("couldn't notify thread");
			} 
		
	}
}
	
	