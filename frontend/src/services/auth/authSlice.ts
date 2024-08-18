import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface AuthState {
  token: string | null;
}

const loadTokenFromStorage = (): string | null => {
  const token = localStorage.getItem('token');

  return token ? JSON.parse(token) : null;
};

const initialState: AuthState = {
  token: loadTokenFromStorage(),
};

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setToken: (state, action: PayloadAction<{ token: string }>) => {
      state.token = action.payload.token;

      localStorage.setItem('token', JSON.stringify(action.payload.token));
    },
    logout: (state) => {
      state.token = null;

      localStorage.removeItem('token');
    },
  },
});

export const { setToken, logout } = authSlice.actions;

export const selectCurrentToken = (state: RootState) => state.authSlice.token;

export default authSlice.reducer;
