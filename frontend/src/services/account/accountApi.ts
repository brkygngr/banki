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

export interface UpdateAccountRequest {
  id: string;
  name: string;
}

export interface DeleteAccountRequest {
  id: string;
}

export const accountApi = createApi({
  reducerPath: 'accountApi',
  tagTypes: ['Accounts'],
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
      invalidatesTags: ["Accounts"]
    }),
    getAccounts: builder.query<GetAccountsResponse, GetAccountsParams>({
      query: (search) => {
        return `?number=${search.number ?? ""}&name=${search.name ?? ""}`;
      },
      providesTags: ["Accounts"]
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
      invalidatesTags: ["Accounts"]
    }),
    deleteAccount: builder.mutation<void, DeleteAccountRequest>({
      query: (request) => ({
        url: `/${request.id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ["Accounts"]
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