import { useState } from 'react';
import { emptyPageable } from '../../models/Pageable';
import {
  CreateAccountRequest,
  DeleteAccountRequest,
  GetAccountsParams,
  UpdateAccountRequest,
  useCreateAccountMutation,
  useDeleteAccountMutation,
  useGetAccountsQuery,
  useUpdateAccountMutation,
} from '../../services/account/accountApi';
import { Accounts } from './Accounts';
import {
  TransferMoneyRequest,
  useTransferMoneyMutation,
} from '../../services/transaction/transactionApi';

export function AccountsContainer() {
  const [getAccountParams, setGetAccountParams] = useState<GetAccountsParams>({});

  const getAccounts = useGetAccountsQuery(getAccountParams);

  const [createAccount] = useCreateAccountMutation();
  const [deleteAccount] = useDeleteAccountMutation();
  const [updateAccount] = useUpdateAccountMutation();
  const [transferMoney] = useTransferMoneyMutation();

  const data = getAccounts.data ?? emptyPageable();

  const handleCreate = async (request: CreateAccountRequest) => {
    await createAccount(request);
  };

  const handleSearch = async (params: GetAccountsParams) => {
    setGetAccountParams(params);
  };

  const handleDelete = async (request: DeleteAccountRequest) => {
    await deleteAccount(request);
  };

  const handleEdit = async (request: UpdateAccountRequest) => {
    await updateAccount(request);
  };

  const handleSend = async (request: TransferMoneyRequest) => {
    await transferMoney(request);

    await getAccounts.refetch();
  };

  return (
    <Accounts
      accountPage={data}
      onCreate={handleCreate}
      onSearch={handleSearch}
      onEdit={handleEdit}
      onDelete={handleDelete}
      onSend={handleSend}
    />
  );
}
