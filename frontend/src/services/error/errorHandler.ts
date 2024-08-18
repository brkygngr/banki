import type { Middleware, MiddlewareAPI } from '@reduxjs/toolkit';
import { isFulfilled, isRejectedWithValue } from '@reduxjs/toolkit';
import { AppToastType } from '../../components/toast/AppToast';
import { logout } from '../auth/authSlice';
import { showToast } from './errorSlice';

interface ApiErrorResponse {
  code: string;
  errors: string[];
  timestamp: string;
}

export const errorHandler: Middleware =
  (api: MiddlewareAPI) => (next) => (action) => {
    if (isRejectedWithValue(action)) {
      const defaultErrorMessage = action.error.message ?? "";

      if (!action.payload || typeof action.payload !== "object") {
        api.dispatch(showToast({ message: defaultErrorMessage, durationMS: 6000, type: AppToastType.FAILURE, header: "Failure" }));

        return;
      }

      let status;

      if ("status" in action.payload) {
        status = action.payload.status
      }

      let response: ApiErrorResponse = {
        errors: [(action.meta.arg as { endpointName: string }).endpointName + " " + defaultErrorMessage],
        code: "",
        timestamp: Date.now().toString()
      }

      if ("data" in action.payload && action.payload.data) {
        response = action.payload.data as { code: string, errors: string[], timestamp: string };
      }

      api.dispatch(showToast({ message: response.code + " " + response.errors, durationMS: 6000, type: AppToastType.FAILURE, header: "Failure" }));

      if (status === 401) {
        api.dispatch(logout());
      }
    }

    if (isFulfilled(action) && (action.meta as any).baseQueryMeta.request.method !== "GET") {
      api.dispatch(showToast({ message: action.type + " " + action.meta.arg, durationMS: 6000, type: AppToastType.SUCCESS, header: "Success" }));
    }

    return next(action)
  }