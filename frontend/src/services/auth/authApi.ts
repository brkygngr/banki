import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export interface LoginRequest {
  identifier: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

interface RegisterResponse {
  userId: string;
}

export const authApi = createApi({
  reducerPath: 'authApi',
  baseQuery: fetchBaseQuery({ baseUrl: process.env.REACT_APP_API_DOMAIN + '/users' }),
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponse, LoginRequest>({
      query: (request) => ({
        url: 'login',
        method: 'POST',
        body: request,
      }),
    }),
    register: builder.mutation<RegisterResponse, RegisterRequest>({
      query: (request) => ({
        url: 'register',
        method: 'POST',
        body: request,
      }),
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation } = authApi;
