import { useState } from "react";
import { emptyPageable } from "../../models/Pageable";
import { CreateAccountRequest, DeleteAccountRequest, GetAccountsParams, useCreateAccountMutation, useDeleteAccountMutation, useGetAccountsQuery } from "../../services/account/accountApi";
import { Accounts } from "./Accounts";

export function AccountsContainer() {
  const [getAccountParams, setGetAccountParams] = useState<GetAccountsParams>({});

  const getAccounts = useGetAccountsQuery(getAccountParams);

  const [createAccount] = useCreateAccountMutation();
  const [deleteAccount] = useDeleteAccountMutation();

  const data = getAccounts.data ?? emptyPageable();

  const handleCreate = async (request: CreateAccountRequest) => {
    await createAccount(request);
  }

  const handleSearch = async (params: GetAccountsParams) => {
    setGetAccountParams(params);
  }

  const handleDelete = async (request: DeleteAccountRequest) => {
    await deleteAccount(request);
  }
  
  return <Accounts accountPage={data} onCreate={handleCreate} onSearch={handleSearch} onDelete={handleDelete} />;
}