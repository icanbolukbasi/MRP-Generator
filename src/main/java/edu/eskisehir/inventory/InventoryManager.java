package edu.eskisehir.inventory;

import edu.eskisehir.utils.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    public InventoryManager(boolean isDefault) {
        if (!isDefault) return;
        try {
            setDefaultItems();
        } catch (IOException e) {
            System.out.println("An unknown error has occurred.");
        }
    }
    
    private void setDefaultItems() throws IOException {
        Path path = Paths.get("items.txt");
        if (!Files.exists(path)) {
            System.out.println("'items.txt' could not be found in the directory of the program.\n" +
                    "Please make sure you have 'items.txt' in the same folder of the program.");
            System.exit(0);
        }
        List<String> lines = Files.readAllLines(path);
        lines.forEach(line -> {
            if (!line.contains("//") && line.length() != 0) {
                String[] information = line.split(",  ");
                //root-item-ID,  Item-ID,  Name of item,  Amount-on-Hand,  Scheduled-Receipt,  Arrival-on-week,  Lead-Time,  Lot-Sizing-Rule,  Multiplier
                int rootID = Integer.parseInt(information[0]);
                int itemID = Integer.parseInt(information[1]);
                String name = information[2];
                int amount = Integer.parseInt(information[3]);
                int scheduledReceipt = Integer.parseInt(information[4]);
                int arrivalOnWeek = Integer.parseInt(information[5]);
                int leadTime = Integer.parseInt(information[6]);
                int lotSizing = Integer.parseInt(information[7]);
                int multiplier = Integer.parseInt(information[8]);
                if (rootID == 0) {
                    Item root = new Item(itemID, name, leadTime, lotSizing, multiplier);
                    Inventory.amounts.put(itemID, amount);
                    Inventory.scheduledReceipts.put(arrivalOnWeek, scheduledReceipt);
                    Inventory.items = new Tree<>(root);
                } else {
                    Inventory.items.find(rootID).addChild(new Item(itemID, name, leadTime, lotSizing, multiplier));
                    Inventory.amounts.put(itemID, amount);
                    Inventory.scheduledReceipts.put(arrivalOnWeek, scheduledReceipt);
                }
            }
        });

    }

    public void setDemands(Map<Integer, Integer> demands) {
        Item root = (Item)Inventory.items.getRoot();
        for (Integer week : demands.keySet()) root.addDemand(week, demands.get(week));
    }

    public void produce() {
        Item root = (Item)Inventory.items.getRoot();
        root.produce();
    }
}
