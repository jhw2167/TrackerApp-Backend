package com.jack.model.dto.mapper;

//Spring Imports
import com.jack.model.PayMethod;
import com.jack.model.UserAccount;
import org.springframework.stereotype.Component;

//Project imports
import com.jack.model.Transaction;
import com.jack.model.dto.TransactionDto;

/**
 * Transaction Mapper utility for mapping between DTO and Entity objects
 *
 * @author Jack Welsh 06/10/2023
 */


@Component
public class TransactionMapper {

    public Transaction toEntity(final TransactionDto dto, final UserAccount u, final PayMethod pm) {
        return new Transaction(dto, u, pm);
    }

    public TransactionDto toDto(Transaction t) {
        TransactionDto dto = new TransactionDto();

        dto.setTid(t.getTid());
        dto.setTrueId(t.getTrueId());
        dto.setUserId(t.getUser().getUserId());

        dto.setPurchaseDate(t.getPurchaseDate());
        dto.setAmount(t.getAmount());
        dto.setVendor(t.getVendor());
        dto.setCategory(t.getCategory());
        dto.setBoughtFor(t.getBoughtFor());
        dto.setPayMethod(t.getPayMethod().getPayMethod());
        dto.setPayStatus(t.getPayStatus());
        dto.setIncome(t.isIncome());
        dto.setReimburses(t.getReimburses());
        dto.setPostedDate(t.getPostedDate());
        dto.setNotes(t.getNotes());

        return dto;
    }
}
