import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { RootState } from '../store';
import { Pageable } from '../../models/Pageable';

export interface Account {
  [key: string]: unknown;
  id: string;
  number: string;
  name: string;
  balance: string;
}

export interface GetAccountsParams {
  number?: string;
  name?: string;
}

interface GetAccountsResponse extends Pageable<Account> { }

interface GetAccountRequest {
  id: string;
}

interface CreateAccountResponse {
  accountId: string;
}

export interface CreateAccountRequest {
  name: string;
}

interface UpdateAccountRequest {
  id: string;
  name: string;
}

interface DeleteAccountRequest {
  id: string;
}

export const accountApi = createApi({
  reducerPath: 'accountApi',
  baseQuery: fetchBaseQuery({
    baseUrl: process.env.REACT_APP_API_DOMAIN + '/accounts',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).authSlice.token;

      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }

      return headers;
    },
  }),
  endpoints: (builder) => ({
    createAccount: builder.mutation<CreateAccountResponse, CreateAccountRequest>({
      query: (request) => ({
        url: '',
        method: 'POST',
        body: request,
      }),
    }),
    getAccounts: builder.query<GetAccountsResponse, GetAccountsParams>({
      query: (search) => {
        return `?number=${search.number ?? ""}&name=${search.name ?? ""}`;
      },
    }),
    getAccount: builder.query<Account, GetAccountRequest>({
      query: ({ id }) => `/${id}`,
    }),
    updateAccount: builder.mutation<void, UpdateAccountRequest>({
      query: ({ id, ...rest }) => ({
        url: `/${id}`,
        method: 'PUT',
        body: rest,
      }),
    }),
    deleteAccount: builder.mutation<void, DeleteAccountRequest>({
      query: (id) => ({
        url: `/${id}`,
        method: 'DELETE',
      }),
    }),
  }),
});

export const {
  useGetAccountsQuery,
  useGetAccountQuery,
  useCreateAccountMutation,
  useUpdateAccountMutation,
  useDeleteAccountMutation,
} = accountApi;