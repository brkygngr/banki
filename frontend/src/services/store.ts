import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import { accountApi } from './account/accountApi';
import { authApi } from './auth/authApi';
import authSliceReducer from './auth/authSlice';
import { errorHandler } from './error/errorHandler';
import errorSliceReducer from './error/errorSlice';
import { transactionApi } from './transaction/transactionApi';

export const store = configureStore({
  reducer: {
    authSlice: authSliceReducer,
    errorSlice: errorSliceReducer,
    [authApi.reducerPath]: authApi.reducer,
    [accountApi.reducerPath]: accountApi.reducer,
    [transactionApi.reducerPath]: transactionApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      authApi.middleware,
      accountApi.middleware,
      transactionApi.middleware,
      errorHandler
    ),
});

setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
