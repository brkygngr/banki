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
    Predicate userPredicate = criteriaBuilder.equal(root.get("user"), accountSearchCriteria.user());
    Predicate numberPredicate = criteriaBuilder.like(root.get("number"), "%" + accountSearchCriteria.number() + "%");
    Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + accountSearchCriteria.name() + "%");

    Predicate numberOrNamePredicate = criteriaBuilder.or(numberPredicate, namePredicate);

    return criteriaBuilder.and(userPredicate, numberOrNamePredicate);
  }
}