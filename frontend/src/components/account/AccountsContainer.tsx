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

export function AccountsContainer() {
  const [getAccountParams, setGetAccountParams] = useState<GetAccountsParams>({});

  const getAccounts = useGetAccountsQuery(getAccountParams);

  const [createAccount] = useCreateAccountMutation();
  const [deleteAccount] = useDeleteAccountMutation();
  const [updateAccount] = useUpdateAccountMutation();

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

  return (
    <Accounts
      accountPage={data}
      onCreate={handleCreate}
      onSearch={handleSearch}
      onEdit={handleEdit}
      onDelete={handleDelete}
    />
  );
}
