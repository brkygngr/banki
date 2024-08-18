import { useParams } from 'react-router-dom';
import { AccountView } from './AccountView';
import { useGetAccountQuery } from '../../services/account/accountApi';
import { useHistoryQuery } from '../../services/transaction/transactionApi';

export function AccountViewContainer() {
  const { id = '' } = useParams();

  const getAccount = useGetAccountQuery({ id });
  const getTransactionHistory = useHistoryQuery({ accountId: id });

  const account = getAccount.data;
  const histories = getTransactionHistory.data ?? [];

  return <AccountView account={account} histories={histories} />;
}
