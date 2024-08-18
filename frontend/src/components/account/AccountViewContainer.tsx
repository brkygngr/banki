import { useParams } from 'react-router-dom';
import { AccountView } from './AccountView';
import { useGetAccountQuery } from '../../services/account/accountApi';

export function AccountViewContainer() {
  const { id = '' } = useParams();

  const getAccount = useGetAccountQuery({ id });

  const account = getAccount.data;

  return <AccountView account={account} />;
}
