package com.jack.service.algorithms.vendors;

//java imports
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//Spring
import org.springframework.beans.factory.annotation.Autowired;

//Project Imports
import com.jack.model.*;
import com.jack.model.enums.*;
import com.jack.repository.TransactionRepo;
import com.jack.repository.VendorRepo;
import org.springframework.stereotype.Component;


/**
 * Vendor Average cost calculating class, handles logic for determining
 * the average transaction cost as it corresponds to each vendor
 *
 * @author Jack Welsh 06/10/2023
 */

@Component
public class AverageCost {

    @Autowired
    VendorRepo vr;

    @Autowired
    TransactionRepo tr;


    public void calculateAverageCostOrIncomeByVendor(Vendor v) {

        //Get all transactions by this vendor
        List<Transaction> allTransactions = tr.findAllByVendor(v);
        List<Transaction> txs = allTransactions.stream()
                .filter(t -> t.getPayStatus() != TransactionStatusType.CANCELLED.getLabel())
                .collect(Collectors.toList());

        if(txs.isEmpty()) {
            v.setAmount(0D);
            v.setTypicallyIncome(false);
            v.setCategory("N/A");
            vr.save(v);
            return;
        }

        //Get all transaction categories associated with this merchant
        //and group by the category and whether this transaction was an expenditure or income
        Map<CategoryStruct, Integer> counter = new HashMap<>();
        for ( Transaction t : txs ) {
            CategoryStruct cs = new CategoryStruct(t.getCategory(), t.isIncome());
            counter.putIfAbsent(cs, 0);
            counter.put(cs, counter.get(cs)+1);
        }

        //Find array item with the highest count
        Map.Entry<CategoryStruct, Integer> mostLikely = counter.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get();

        //Calculate median income or cost from this merchant
        Predicate<Transaction> p = t ->
                t.getCategory().equals( mostLikely.getKey().category ) &&
                        t.isIncome() == mostLikely.getKey().isIncome;
        List<Double> amounts = Arrays.stream(txs.stream()
                .filter(p)
                .mapToDouble(Transaction::getAmount).toArray())
                .boxed().collect(Collectors.toList());
        double median = amounts.size() % 2 == 0
                ? (amounts.get(amounts.size() / 2 - 1) + amounts.get(amounts.size() / 2)) / 2.0
                : amounts.get(amounts.size() / 2);

        //save to vendors table
        v.setCategory(mostLikely.getKey().category);
        v.setTypicallyIncome(mostLikely.getKey().isIncome);
        v.setAmount(median);

        vr.save(v);
    }

    public class CategoryStruct {
        String category;
        Boolean isIncome;

        public CategoryStruct(String category, Boolean isIncome) {
            this.category = category;
            this.isIncome = isIncome;
        }
    }

}
