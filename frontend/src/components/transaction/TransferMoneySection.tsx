import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Form, InputGroup } from 'react-bootstrap';
import { Account } from '../../services/account/accountApi';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faMoneyBillTrendUp } from '@fortawesome/free-solid-svg-icons';
import { FormEvent, useState } from 'react';
import { TransferMoneyRequest } from '../../services/transaction/transactionApi';

library.add(faMoneyBillTrendUp);

export interface TransferMoneySectionProps {
  accounts: Account[];
  onSend: (request: TransferMoneyRequest) => Promise<void>;
}

export function TransferMoneySection({ accounts, onSend }: TransferMoneySectionProps) {
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [amount, setAmount] = useState(0);

  const options = accounts.map((account) => {
    return {
      value: account.id,
      text: account.name,
    };
  });
  options.unshift({ value: '', text: '' });

  const optionEls = options.map((option) => (
    <option key={option.value} value={option.value}>
      {option.text}
    </option>
  ));

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (from === to) {
      alert('Accounts must be different');

      return;
    }

    await onSend({ from, to, amount });
  };

  return (
    <section className="my-2">
      <Form onSubmit={handleSubmit}>
        <InputGroup>
          <InputGroup.Text>From</InputGroup.Text>
          <Form.Select required value={from} onChange={(e) => setFrom(e.target.value)}>
            {optionEls}
          </Form.Select>
          <InputGroup.Text>To</InputGroup.Text>
          <Form.Select required value={to} onChange={(e) => setTo(e.target.value)}>
            {optionEls}
          </Form.Select>
          <InputGroup.Text>Amount</InputGroup.Text>
          <Form.Control
            type="number"
            min={0}
            value={amount}
            onChange={(e) => setAmount(parseInt(e.target.value))}
            required
          />
          <Button variant="primary" type="submit">
            <FontAwesomeIcon className="me-1" icon="money-bill-trend-up" />
            Send
          </Button>
        </InputGroup>
      </Form>
    </section>
  );
}
