import { Container, InputGroup, Spinner } from 'react-bootstrap';
import { Account } from '../../services/account/accountApi';

export interface AccountViewProps {
  account: Account | undefined;
}

export function AccountView({ account }: AccountViewProps) {
  if (!account) {
    return <Spinner></Spinner>;
  }

  return (
    <Container>
      <h2>{account.number}</h2>
      <section>
        <Container>
          <InputGroup>Name: {account.name}</InputGroup>
          <p>Balance: {account.balance}</p>
        </Container>
      </section>
    </Container>
  );
}
