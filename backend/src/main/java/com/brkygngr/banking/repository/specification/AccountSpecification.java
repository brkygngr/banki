package com.brkygngr.banking.repository.specification;

import com.brkygngr.banking.entity.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class AccountSpecification implements Specification<Account> {

  private final AccountSearchCriteria accountSearchCriteria;

  @Override
  public Predicate toPredicate(Root<Account> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    String number = accountSearchCriteria.number();
    String name = accountSearchCriteria.name();

    Predicate userPredicate = criteriaBuilder.equal(root.get("user"), accountSearchCriteria.user());

    if (number.isBlank() && name.isBlank()) {
      return userPredicate;
    }

    Predicate numberPredicate = criteriaBuilder.like(root.get("number"), "%" + number + "%");
    Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + name + "%");

    if (number.isBlank()) {
      return criteriaBuilder.and(userPredicate, namePredicate);
    }

    if (name.isBlank()) {
      return criteriaBuilder.and(userPredicate, numberPredicate);
    }

    Predicate numberOrNamePredicate = criteriaBuilder.and(numberPredicate, namePredicate);

    return criteriaBuilder.and(userPredicate, numberOrNamePredicate);
  }
}