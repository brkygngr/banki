package com.brkygngr.banking.repository.specification;

import com.brkygngr.banking.entity.User;

public record AccountSearchCriteria(User user, String number, String name) {

}
