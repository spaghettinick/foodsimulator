package store;

import customer.StoreObserver;
import roll.*;

import java.util.ArrayList;
import java.util.HashMap;

public class RollStore implements StoreObservable {
    RollFactory rollFactory;
    ArrayList<StoreObserver> customers;
    HashMap<String, Integer> numSold;
    HashMap<String, ArrayList<Roll>> inventory;
    private double totalSales = 0.0;

    public RollStore(RollFactory factory){
        this.rollFactory = factory;
        this.customers = new ArrayList<>();

        // init inventory, which is a map of strings to Roll array
        this.inventory = new HashMap<>();
        ArrayList<Roll> initEggRolls = this.restockRoll("egg");
        ArrayList<Roll> initJellyRolls = this.restockRoll("jelly");
        ArrayList<Roll> initPastryRolls = this.restockRoll("pastry");
        ArrayList<Roll> initSausageRolls = this.restockRoll("sausage");
        ArrayList<Roll> initSpringRolls = this.restockRoll("spring");

        this.inventory.put("egg", initEggRolls);
        this.inventory.put("jelly", initJellyRolls);
        this.inventory.put("pastry", initPastryRolls);
        this.inventory.put("sausage", initSausageRolls);
        this.inventory.put("spring", initSpringRolls);
    }

    public void openForADay(int day) {
        System.out.printf("\n== Open For day %d ==\n", day);
        // reset number of rolls sold that day
        this.numSold = new HashMap<String, Integer>(){{
            put("egg", 0); put("jelly", 0); put("pastry", 0); put("sausage", 0); put("spring", 0);
        }};

        // TODO: check and restock empty roll stores

        // take customers
        this.notifyObservers();

        // print everything at the end of the day
        this.getInventory();
        this.getSalesData();
        System.out.printf("== Closing on day %d ==\n", day);
    }

    /********* Getters and setters ***********/
    public void getSalesData(){

        StringBuilder out = new StringBuilder();
        out.append("Sales Data:\n");
        for (String roll: this.numSold.keySet()){
            out.append("Number of ").append(roll).append(" rolls sold: ").append(this.numSold.get(roll)).append("\n");
        }
        out.append("Total Sales = ").append(totalSales).append("\n");
        System.out.println(out);
    }

    public void getInventory() {
        StringBuilder out = new StringBuilder();
        out.append("Inventory Data:\n");
        for (String roll: this.inventory.keySet()){
            out.append("Number of ").append(roll).append(" rolls left: ").append(this.inventory.get(roll).size()).append("\n");
        }
        System.out.println(out);
    }

    /********** functions for making orders ***********/
    // create a new roll from factory
    private Roll createRoll(String rollType) {
        Roll roll;
        roll = rollFactory.createRoll(rollType);
        return roll;
    }

    private ArrayList<Roll> restockRoll(String rollType) {
        ArrayList<Roll> rolls = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            rolls.add(this.createRoll(rollType));
        }
        return rolls;
    }

    private void makeOrder(HashMap<String, Integer> order) {
        if (order == null) {
            return;
        }
        // take the order from inventory
        for (String roll: order.keySet()) {
            for (int i = 0; i < order.get(roll); i++) {
                int index = this.inventory.get(roll).size()-1;
                Roll purchasedRoll = this.inventory.get(roll).remove(index);
                // record total sales
                this.totalSales += purchasedRoll.cost();
                // record the rolls sold
                this.numSold.put(roll,this.numSold.get(roll) + 1);
            }
        }
    }

    // make sure we have the proper order
    private boolean validateOrder(HashMap<String, Integer> order) {
        for (String roll: order.keySet()){
            // invalidate if there are more in the order than are left in the inventory left;
            if (order.get(roll) > inventory.get(roll).size()) {
                return false;
            }
        }
        return true;
    }


    /********** Observable functions ************/
    // add customers to the store
    public void addObserver(StoreObserver customer) {
        this.customers.add(customer);
    }

    // remove customers
    public void removeObserver(StoreObserver customer) {
        this.customers.remove(customer);
    }

    // tell observers store is open, get orders.
    public void notifyObservers() {
        for (StoreObserver customer: this.customers) {
            HashMap<String, Integer> order =  customer.makeRollOrder();
            if (!this.validateOrder(order)) {
                // TODO: create another store observer function that handles an invalidated order
                System.out.println("Could not full fill order");
                order = null;
            }
            this.makeOrder(order);
        }
    }

    // ???
    public void setChanged() { }
}
