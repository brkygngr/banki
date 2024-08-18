import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { AppToastType } from "../../components/toast/AppToast";

export interface ErrorMessage {
  header: string;
  message: string;
  timestamp: number;
  durationMS: number;
  type: AppToastType;
}

interface ErrorState {
  messages: ErrorMessage[];
}

const initialState: ErrorState = {
  messages: [],
};

export const authSlice = createSlice({
  name: 'error',
  initialState,
  reducers: {
    showToast: (state, action: PayloadAction<{ message: string, durationMS: number, type: AppToastType, header: string }>) => {
      state.messages.push({
        message: action.payload.message, durationMS: action.payload.durationMS, timestamp: Date.now(), type: action.payload.type,
        header: action.payload.header
      })
    },
    deleteMessage: (state, action: PayloadAction<{ message: ErrorMessage }>) => {
      const index = state.messages.findIndex((message) => message.timestamp === action.payload.message.timestamp);

      state.messages.splice(index, 1);
    }
  },
});

export const { showToast, deleteMessage } = authSlice.actions;

export default authSlice.reducer;
