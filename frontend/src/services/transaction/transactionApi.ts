import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { Account } from '../account/accountApi';
import { RootState } from '../store';

export const enum TransactionStatus {
  SUCCESS,
  FAILED,
}

export interface TransactionHistoryResponse {
  id: string;
  from: Account;
  to: Account;
  amount: number;
  transactionDate: Date;
  status: TransactionStatus;
}

export interface TransferMoneyRequest {
  from: string;
  to: string;
  amount: number;
}

export interface TransactionHistoryRequest {
  accountId: string;
}

export const transactionApi = createApi({
  reducerPath: 'transactionApi',
  baseQuery: fetchBaseQuery({
    baseUrl: process.env.REACT_APP_API_DOMAIN + '/transactions',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).authSlice.token;

      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }

      return headers;
    },
  }),
  endpoints: (builder) => ({
    transferMoney: builder.mutation<void, TransferMoneyRequest>({
      query: (request) => ({
        url: '/transfer',
        method: 'POST',
        body: request,
      }),
    }),
    history: builder.query<TransactionHistoryResponse[], TransactionHistoryRequest>({
      query: (request) => ({
        url: `/account/${request.accountId}`,
        method: 'GET',
      }),
    }),
  }),
});

export const { useTransferMoneyMutation, useHistoryQuery } = transactionApi;
