package customer;

import java.util.HashMap;

public interface StoreObserver {
   HashMap<String, Integer> makeRollOrder();
//   HashMap<String, Integer> makeModifiedRollOrder();
}
