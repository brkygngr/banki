import { useState } from "react";
import { emptyPageable } from "../../models/Pageable";
import { CreateAccountRequest, GetAccountsParams, useCreateAccountMutation, useGetAccountsQuery } from "../../services/account/accountApi";
import { Accounts } from "./Accounts";

export function AccountsContainer() {
  const [getAccountParams, setGetAccountParams] = useState<GetAccountsParams>({});

  const getAccounts = useGetAccountsQuery(getAccountParams);

  const [createAccount] = useCreateAccountMutation();

  const data = getAccounts.data ?? emptyPageable();

  const handleCreate = async (payload: CreateAccountRequest) => {
    await createAccount(payload);
  }

  const handleSearch = async (params: GetAccountsParams) => {
    if (!params.name && !params.number) {
      return;
    }

    setGetAccountParams(params);
  }
  
  return <Accounts accountPage={data} onCreate={handleCreate} onSearch={handleSearch} />;
}