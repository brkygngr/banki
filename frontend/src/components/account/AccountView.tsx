import { Container, Spinner } from 'react-bootstrap';
import { Account } from '../../services/account/accountApi';
import { TransactionHistoryResponse } from '../../services/transaction/transactionApi';
import { TransactionHistoryTable } from '../transaction/TransactionHistoryTable';

export interface AccountViewProps {
  account: Account | undefined;
  histories: TransactionHistoryResponse[];
}

export function AccountView({ account, histories }: AccountViewProps) {
  if (!account) {
    return (
      <Container className="justify-content-center align-items-center">
        <Spinner></Spinner>
      </Container>
    );
  }

  return (
    <Container className="g-0">
      <h2 className="my-2">
        <strong>Account number:</strong> {account.number}
      </h2>
      <section className="my-2">
        <TransactionHistoryTable histories={histories} />
      </section>
    </Container>
  );
}
